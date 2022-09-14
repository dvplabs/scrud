package example.input.mapper;

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
@RequestMapping(path = "/v1/products")
public class ProductsCrudController {

  @Autowired
  private EntityManager entityManager;
  @Autowired
  private PartialMapper mapper;
  
  @PutMapping(path = "/{id}")
  @ResponseStatus(HttpStatus.OK)
  @Transactional
  public void update(@RequestBody ProductDto myDto, @PathVariable Long id) {
    var myModel = entityManager.find(Product.class, id);
    if (Objects.isNull(myModel)) {
      throw new EntityNotFoundException("Entity not found");
    }

    mapper.updateEntity(myModel, myDto);
  }

  @GetMapping(path = "/{id}")
  @ResponseStatus(HttpStatus.OK)
  @Transactional(readOnly = true)
  public ProductDto read(@PathVariable Long id) {
    var myModel = entityManager.find(Product.class, id);
    if (Objects.isNull(myModel)) {
      throw new EntityNotFoundException("Entity not found");
    }

    return mapper.toDto(myModel);
  }
}
