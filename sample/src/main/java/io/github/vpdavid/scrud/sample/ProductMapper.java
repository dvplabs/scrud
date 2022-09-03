package io.github.vpdavid.scrud.sample;

import io.github.vpdavid.scrud.Crud;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 *
 * @author david
 */
@Mapper(componentModel = "spring")
@Crud(resource = "/products", model = Product.class, dto = ProductDto.class)
public interface ProductMapper {
  
  ProductDto toDto(Product model);
  
  @Mapping(target = "id", ignore = true)
  void updateData(@MappingTarget Product model, ProductDto dto);
  
  default void assertDeletion(Product model) {
    
  }
}
