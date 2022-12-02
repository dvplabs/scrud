package example.input.mapper;

import io.github.dvplabs.scrud.Crud;
import io.github.dvplabs.scrud.util.PutMapping;

@Crud(resource = "/products",  
    model = example.input.model.Product.class, 
    dto = example.input.dto.Product.class)
public interface MapperWithClashingDependencies {
  
  example.input.dto.Product toDto(example.input.model.Product model);
  
  void updateEntity(
      example.input.model.Product myModel, 
      example.input.dto.Product myDto, 
      /*Custom class*/ PutMapping data);
  
  void assertRemovable(example.input.model.Product model);

  
}