package io.github.vpdavid.scrud;

import com.google.testing.compile.Compilation;
import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;
import com.google.testing.compile.JavaFileObjects;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import static java.util.stream.Collectors.toList;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
  void generateFullControllerForResource() throws IOException {
    generateController(
        "example/input/mapper/BasicMapper.java", 
        "example/output/BasicController.java");
  }
  
  @Test
  void generatePartialControllerForResource() throws IOException {
    generateController(
        "example/input/mapper/PartialMapper.java", 
        "example/output/PartialController.java");
  }
  
  @Test
  void generateControllerWithExtraParams() throws IOException {
    generateController(
        "example/input/mapper/MapperWithExtraParams.java", 
        "example/output/ControllerWithExtraParams.java");
  }
  
  @Test
  void errorWhenNoAvailableMethodForVerb() throws IOException {
    failsWithMessage(
        "example/input/mapper/MissingPutMapper.java",
        "No suitable method found for PUT operation.");
  }
  
  @Test
  void errorWhenTooManyMethodsForVerb() throws IOException {
    failsWithMessage(
        "example/input/mapper/TooManyGetsMapper.java", 
        "Too many methods found for GET operation: toDto, transform.");
  }
  
  @Test
  void avoidsNameClashing() throws IOException {
    generateController(
        "example/input/mapper/MapperWithClashingDependencies.java", 
        "example/output/ControllerWithClashingDependencies.java");
  }
  
  void failsWithMessage(String file, String msg) throws IOException {
    var files = Stream.of(
        "example/input/model/Product.java",
        "example/input/dto/ProductDto.java",
        file)
        .map(JavaFileObjects::forResource)
        .collect(toList());
    
    Compilation compilation = javac()
        .withProcessors(new CrudGenerator())
        .withClasspath(classPath)
        .compile(files);
    
    assertThat(compilation).hadErrorContaining(msg);
  }
  
  void generateController(String mapperPath, String resultPath) throws IOException {
    var files = Stream.of(
          "example/input/model/Product.java", 
          "example/input/dto/ProductDto.java",
          mapperPath)
        .map(JavaFileObjects::forResource)
        .collect(toList());
    
    Compilation compilation = javac()
        .withProcessors(new CrudGenerator())
        .withClasspath(classPath)
        .compile(files);
    
    assertThat(compilation)
        .generatedSourceFile("example.input.mapper.ProductsCrudController")
        .hasSourceEquivalentTo(JavaFileObjects.forResource(resultPath));
  }
}
