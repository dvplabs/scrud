package example.input.mapper;

import example.input.dto.ProductDto;
import example.input.model.Product;
import java.util.Objects;
import javax.annotation.processing.Generated;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Generated(value = "io.github.vpdavid.scrud.CrudGenerator", date = "2022-05-12 18:23:12")
@RestController
@RequestMapping(path = "/products")
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
