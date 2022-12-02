package io.github.dvplabs.scrud.sample;

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
