/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package io.github.vpdavid.scrud;

import com.google.testing.compile.Compilation;
import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;
import com.google.testing.compile.JavaFileObjects;
import java.util.Set;
import static java.util.stream.Collectors.toList;
import java.util.stream.Stream;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 *
 * @author david
 */
public class CrudGeneratorTest {
  
  
  @Test
  void generatesControllerForResource() {
    var files = Stream.of("Mapper.java", "Product.java", "ProductDto.java")
        .map(JavaFileObjects::forResource)
        .collect(toList());
    Compilation compilation = javac()
        .withProcessors(new CrudGenerator())
        .compile(files);
    
    assertThat(compilation)
        .generatedSourceFile("io.github.vpdavid.scrud.ProductsCrudController");
  }
}
