package example.input.mapper;

import example.input.model.Product;
import example.input.dto.ProductDto;
import io.github.vpdavid.scrud.*;

@Crud(resource = "/v1/products", 
    verbs = {Verb.PUT, Verb.GET}, 
    model = Product.class, 
    dto = ProductDto.class)
public interface PartialMapper {

  ProductDto toDto(Product myModel);

  void updateEntity(Product myModel, ProductDto myDto);
}