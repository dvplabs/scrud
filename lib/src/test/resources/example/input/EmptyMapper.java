package example.input;

import example.model.Product;
import example.dto.ProductDto;
import io.github.vpdavid.scrud.*;

//@Crud(resource = "/v1/products", verbs = {})
public class EmptyMapper {

  public ProductDto toDto(Product model) {
    throw new UnsupportedOperationException();
  }

  public void updateEntity(Product model, ProductDto dto) {
    throw new UnsupportedOperationException();
  }
}
