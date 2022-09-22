package example.input.mapper;

import example.input.model.Product;
import example.input.dto.ProductDto;
import io.github.vpdavid.scrud.*;

@Crud(resource = "/products", model = Product.class, dto = ProductDto.class, verbs = {Verb.GET})
public interface TooManyGetsMapper {
  
  ProductDto toDto(Product model);

  ProductDto transform(Product model);
}
