package example.input.mapper;

import io.github.dvplabs.scrud.Crud;
import example.input.model.Product;
import example.input.dto.ProductDto;
import org.springframework.lang.NonNull;

@Crud(resource = "/products", model = Product.class, dto = ProductDto.class)
public interface BasicMapper {
  
  ProductDto toDto(Product model);
  
  void updateEntity(@NonNull Product model, ProductDto dto);
  
  default void assertRemovable(Product model) {
    
  }
}
