package example.input.mapper;

import example.input.dto.ProductDto;
import example.input.model.Product;
import io.github.dvplabs.scrud.Crud;

@Crud(resource = "/21live/3/v1/info", dto = ProductDto.class, model = Product.class, verbs = {})
public class LongResourcePath {
  
}