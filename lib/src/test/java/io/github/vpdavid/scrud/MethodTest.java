package io.github.vpdavid.scrud;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 *
 * @author david
 */
public class MethodTest {
  
  @Test
  void readSource() throws IOException, URISyntaxException {
    verifyContents(Method.GET, Path.of("src/test/resources/get.txt"));
    verifyContents(Method.GET_ALL, Path.of("src/test/resources/get-all.txt"));
    verifyContents(Method.POST, Path.of("src/test/resources/post.txt"));
    verifyContents(Method.PUT, Path.of("src/test/resources/put.txt"));
    verifyContents(Method.DELETE, Path.of("src/test/resources/delete.txt"));
  }
  
  void verifyContents(Method method, Path template) throws IOException, URISyntaxException {
    var src = Files.lines(template)
        .map(l -> "\t" + l)
        .collect(Collectors.joining("\n"));
    
    assertEquals(src, method.getSource(1, "Product", "ProductDto"));
  }
  
  
}
