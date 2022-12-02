package example.input.mapper;

import io.github.dvplabs.scrud.Crud;
import io.github.dvplabs.scrud.Verb;
import example.input.model.Product;
import example.input.dto.ProductDto;

@Crud(resource = "/products", model = Product.class, dto = ProductDto.class, verbs = {Verb.GET})
public interface TooManyGetsMapper {
  
  ProductDto toDto(Product model);

  ProductDto transform(Product model);
}
