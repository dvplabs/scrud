package io.github.vpdavid.scrud;

import com.google.testing.compile.Compilation;
import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;
import com.google.testing.compile.JavaFileObjects;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import static java.util.stream.Collectors.toList;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author david
 */
public class CrudGeneratorTest {
  List<File> classPath;
  
  @BeforeEach
  void init() {
    var path = System.getProperty("java.class.path");
    var separator = System.getProperty("path.separator");
    classPath = Arrays.stream(path.split(separator))
        .map(File::new)
        .collect(toList());
  }
  
  @Test
  void generatesEmptyControllerForResource() {
    generateController("example/EmptyMapper.java", "expected/EmptyController.java");
  }
  
  @Test
  void generateFullControllerForResource() {
    generateController("example/FullMapper.java", "expected/FullController.java");
  }
  
  @Test
  void generatePartialControllerForResource() {
    generateController("example/PartialMapper.java", "expected/PartialController.java");
  }
  
  void generateController(String mapperPath, String resultPath) {
    var files = Stream.of(
          "example/model/Product.java", 
          "example/dto/ProductDto.java",
          mapperPath)
        .map(JavaFileObjects::forResource)
        .collect(toList());
    
    Compilation compilation = javac()
        .withProcessors(new CrudGenerator())
        .withClasspath(classPath)
        .compile(files);
    
    assertThat(compilation)
        .generatedSourceFile("example.ProductsCrudController")
        .hasSourceEquivalentTo(JavaFileObjects.forResource(resultPath));
  }
}
