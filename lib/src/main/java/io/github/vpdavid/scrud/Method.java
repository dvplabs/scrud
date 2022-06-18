package io.github.vpdavid.scrud;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import static java.util.stream.Collectors.joining;
import java.util.stream.IntStream;

/**
 *
 * @author david
 */
public enum Method {
  POST("post.tpl"), 
  GET("get.tpl"), 
  PUT("put.tpl"), 
  DELETE("delete.tpl"), 
  GET_ALL("get-all.tpl");
  
  private final String TEMPLATE_NAME;
  
  Method(String templateName) {
    this.TEMPLATE_NAME = templateName;
  }
  
  String getSource(int tabulationCount) throws IOException, URISyntaxException {
    var url = getClass().getClassLoader().getResource(TEMPLATE_NAME);
    return Files.lines(Path.of(url.toURI()))
        .map(l -> tabs(tabulationCount) + l)
        .collect(joining("\n"));
  }
  
  private String tabs(int count) {
    return IntStream.range(0, count)
        .mapToObj(i -> "\t")
        .collect(joining(""));
  }
}
