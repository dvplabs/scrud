package example.input;

import example.input.model.Product;
import example.input.dto.ProductDto;
import io.github.vpdavid.scrud.*;

@Crud(resource = "/products", model = Product.class, dto = ProductDto.class)
public class BasicMapper {
  
  public ProductDto toDto(Product model) {
    throw new UnsupportedOperationException();
  }
  
  public void updateEntity(Product model, ProductDto dto) {
    throw new UnsupportedOperationException();
  }
  
  public void assertRemovable(Product model) {
    throw new UnsupportedOperationException();
  }
}
