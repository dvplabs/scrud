package example.input;

import example.input.model.Product;
import example.input.dto.ProductDto;
import io.github.vpdavid.scrud.*;
import org.springframework.lang.NonNull;

@Crud(resource = "/products", model = Product.class, dto = ProductDto.class)
public interface BasicMapper {
  
  ProductDto toDto(Product model);
  
  void updateEntity(@NonNull Product model, ProductDto dto);
  
  default void assertRemovable(Product model) {
    
  }
}
