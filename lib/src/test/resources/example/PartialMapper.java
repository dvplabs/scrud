package example;

import example.model.Product;
import example.dto.ProductDto;
import io.github.vpdavid.scrud.*;

@Crud(resource = "/v1/products", methods = {Method.PUT, Method.GET})
public class PartialMapper implements ResourceMapper<Product, ProductDto> {

  @Override
  public ProductDto toDto(Product model) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void updateEntity(Product model, ProductDto dto) {
    throw new UnsupportedOperationException();
  }
}