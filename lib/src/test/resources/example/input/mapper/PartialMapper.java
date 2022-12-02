package example.input.mapper;

import io.github.dvplabs.scrud.Crud;
import io.github.dvplabs.scrud.Verb;
import example.input.model.Product;
import example.input.dto.ProductDto;

@Crud(resource = "/products", 
    verbs = {Verb.PUT, Verb.GET}, 
    model = Product.class, 
    dto = ProductDto.class)
public interface PartialMapper {

  ProductDto toDto(Product myModel);

  void updateEntity(Product myModel, ProductDto myDto);
}