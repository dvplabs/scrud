/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package io.github.vpdavid.scrud;

import javax.annotation.processing.ProcessingEnvironment;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 *
 * @author david
 */
@ExtendWith(MockitoExtension.class)
public class CrudGeneratorTest {
  
  @Mock
  ProcessingEnvironment processingEnv;
  
  CrudGenerator crudGenerator;
  
  @BeforeEach
  void init() {
    crudGenerator = new CrudGenerator();
    crudGenerator.init(processingEnv);
  }
  
  @Test
  void initializes() {
    assertNotNull(crudGenerator);
  }
}
