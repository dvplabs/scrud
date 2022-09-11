package example.input;

import java.util.*;
import java.util.stream.*;
import javax.persistence.*;
import javax.persistence.criteria.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import example.input.model.Product;
import example.input.dto.ProductDto;

@RestController
@RequestMapping(path = "/products")
public class ProductsCrudController {

  @Autowired
  private EntityManager entityManager;
  @Autowired
  private BasicMapper mapper;

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  @Transactional(readOnly = true)
  public Page<ProductDto> read(Pageable pageable) {
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
      .map(o -> mapper.toDto((Product)o))
      .collect(Collectors.toList());
    return new PageImpl(results, pageable, total);
  }

  @GetMapping(path = "/{id}")
  @ResponseStatus(HttpStatus.OK)
  @Transactional(readOnly = true)
  public ProductDto read(@PathVariable Long id) {
    var model = entityManager.find(Product.class, id);
    if (Objects.isNull(model)) {
      throw new EntityNotFoundException("Entity not found");
    }

    return mapper.toDto(model);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Transactional
  public void create(@RequestBody ProductDto dto) {
    var model = new Product();
    mapper.updateEntity(model, dto);
    entityManager.persist(model);
  }

  @PutMapping(path = "/{id}")
  @ResponseStatus(HttpStatus.OK)
  @Transactional
  public void update(@RequestBody ProductDto dto, @PathVariable Long id) {
    var model = entityManager.find(Product.class, id);
    if (Objects.isNull(model)) {
      throw new EntityNotFoundException("Entity not found");
    }

    mapper.updateEntity(model, dto);
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
