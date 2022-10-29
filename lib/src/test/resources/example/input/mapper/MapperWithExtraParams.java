package example.input.mapper;

import io.github.vpdavid.scrud.util.CustomSession;
import example.input.model.Product;
import example.input.dto.ProductDto;
import io.github.vpdavid.scrud.*;
import jakarta.servlet.http.HttpServletRequest;

@Crud(resource = "/products", model = Product.class, dto = ProductDto.class)
public interface MapperWithExtraParams {

  ProductDto toDto(CustomSession mySession, Product model);

  void updateEntity(ProductDto someDto, CustomSession customSession, Product someModel, HttpServletRequest request);
  
  void assertDelete(Product myModel, CustomSession session);
}
