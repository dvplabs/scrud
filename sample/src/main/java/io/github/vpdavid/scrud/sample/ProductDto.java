/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package io.github.vpdavid.scrud.sample;

import lombok.Data;
import lombok.Builder;

/**
 *
 * @author david
 */
@Data
@Builder
public class ProductDto {
  private Long id;
  private String name;
}
