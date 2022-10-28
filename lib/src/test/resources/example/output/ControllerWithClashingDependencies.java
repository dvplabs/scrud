package example.input.mapper;

import example.input.model.Product;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.processing.Generated;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Generated(value = "io.github.vpdavid.scrud.CrudGenerator", date = "2022-05-12 18:23:12")
@RestController
@RequestMapping(path = "/v1/products")
public class ProductsCrudController {

  @Autowired
  private EntityManager entityManager;
  @Autowired
  private MapperWithClashingDependencies mapper;

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  @Transactional(readOnly = true)
  public Page<example.input.dto.Product> read(Pageable pageable) {
    var cb = entityManager.getCriteriaBuilder();

    var cqTotal = cb.createQuery(Long.class);
    var selectTotal = cqTotal.select(cb.count(cqTotal.from(Product.class)));
    Long total = entityManager.createQuery(selectTotal).getSingleResult();

    var cq = cb.createQuery(Product.class);
    var root = cq.from(Product.class);
    cq.select(root);

    if (pageable.getSort().isSorted()) {
      var orders = new ArrayList<Order>();
      for (var order : pageable.getSort()) {
        if (order.isAscending()) {
          orders.add(cb.asc(root.get(order.getProperty())));
        } else {
          orders.add(cb.desc(root.get(order.getProperty())));
        }
      }
      cq.orderBy(orders);
    }

    var query = entityManager.createQuery(cq);
    query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
    query.setMaxResults(pageable.getPageSize());
    var results = query.getResultList().stream()
      .map(model -> mapper.toDto(model))
      .collect(Collectors.toList());
    return new PageImpl(results, pageable, total);
  }

  @GetMapping(path = "/{id}")
  @ResponseStatus(HttpStatus.OK)
  @Transactional(readOnly = true)
  public example.input.dto.Product read(@PathVariable Long id) {
    var model = entityManager.find(Product.class, id);
    if (Objects.isNull(model)) {
      throw new EntityNotFoundException("Entity not found");
    }

    return mapper.toDto(model);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Transactional
  public void create(@RequestBody example.input.dto.Product myDto, io.github.vpdavid.scrud.util.PutMapping data) {
    var myModel = new Product();
    mapper.updateEntity(myModel, myDto, data);
    entityManager.persist(myModel);
  }

  @PutMapping(path = "/{id}")
  @ResponseStatus(HttpStatus.OK)
  @Transactional
  public void update(@RequestBody example.input.dto.Product myDto, @PathVariable Long id, io.github.vpdavid.scrud.util.PutMapping data) {
    var myModel = entityManager.find(Product.class, id);
    if (Objects.isNull(myModel)) {
      throw new EntityNotFoundException("Entity not found");
    }

    mapper.updateEntity(myModel, myDto, data);
  }

  @DeleteMapping(path = "/{id}")
  @ResponseStatus(HttpStatus.OK)
  @Transactional
  public void delete(@PathVariable Long id) {
    var model = entityManager.find(Product.class, id);
    if (Objects.isNull(model)) {
      throw new EntityNotFoundException("Entity not found");
    }

    mapper.assertRemovable(model);
    entityManager.remove(model);
  }
}