package example.input.mapper;

import example.input.dto.ProductDto;
import example.input.model.Product;
import io.github.dvplabs.scrud.Crud;
import io.github.dvplabs.scrud.Verb;

@Crud(resource = "/products", model = Product.class, dto = ProductDto.class, verbs = {Verb.PUT})
public interface MissingPutMapper {
  ProductDto toDto(Product model);
}
