package example.input.mapper;

import example.input.dto.ProductDto;
import example.input.model.Product;
import io.github.vpdavid.scrud.util.CustomSession;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;
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

@RestController
@RequestMapping(path = "/v1/products")
public class ProductsCrudController {

  @Autowired
  private EntityManager entityManager;
  @Autowired
  private MapperWithExtraParams mapper;
  
  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  @Transactional(readOnly = true)
  public Page<ProductDto> read(Pageable pageable, CustomSession mySession) {
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
      .map(model -> mapper.toDto(mySession, model))
      .collect(Collectors.toList());
    return new PageImpl(results, pageable, total);
  }

  @GetMapping(path = "/{id}")
  @ResponseStatus(HttpStatus.OK)
  @Transactional(readOnly = true)
  public ProductDto read(@PathVariable Long id, CustomSession mySession) {
    var model = entityManager.find(Product.class, id);
    if (Objects.isNull(model)) {
      throw new EntityNotFoundException("Entity not found");
    }

    return mapper.toDto(mySession, model);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Transactional
  public void create(@RequestBody ProductDto someDto, CustomSession customSession, HttpServletRequest request) {
    var someModel = new Product();
    mapper.updateEntity(someDto, customSession, someModel, request);
    entityManager.persist(someModel);
  }

  @PutMapping(path = "/{id}")
  @ResponseStatus(HttpStatus.OK)
  @Transactional
  public void update(@RequestBody ProductDto someDto, @PathVariable Long id, CustomSession customSession, HttpServletRequest request) {
    var someModel = entityManager.find(Product.class, id);
    if (Objects.isNull(someModel)) {
      throw new EntityNotFoundException("Entity not found");
    }

    mapper.updateEntity(someDto, customSession, someModel, request);
  }

  @DeleteMapping(path = "/{id}")
  @ResponseStatus(HttpStatus.OK)
  @Transactional
  public void delete(@PathVariable Long id, CustomSession session) {
    var myModel = entityManager.find(Product.class, id);
    if (Objects.isNull(myModel)) {
      throw new EntityNotFoundException("Entity not found");
    }

    mapper.assertDelete(myModel, session);
    entityManager.remove(myModel);
  }
}