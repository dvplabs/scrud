/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import io.github.vpdavid.scrud.*;

/**
 *
 * @author david
 */
  @Crud(resource = "/products")
  class Mapper implements ResourceMapper<Product, ProductDto> {
    
    @Override
    public ProductDto toDto(Product model) {
      throw new UnsupportedOperationException();
    }
    
    @Override
    public void updateEntity(Product model, ProductDto dto) {
      throw new UnsupportedOperationException();
    }
  }
