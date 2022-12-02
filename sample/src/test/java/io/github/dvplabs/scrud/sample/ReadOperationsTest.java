package io.github.dvplabs.scrud.sample;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

/**
 *
 * @author david
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReadOperationsTest {
  @LocalServerPort
  int port;
  @Autowired
  TestRestTemplate restTemplate;
  String baseUrl;
  
  @BeforeEach
  void setup() {
    baseUrl = "http://localhost:" + port + "/products";
  }
  
  @Test
  void readsWithDefaultPagination() throws JsonProcessingException {
    checkPage(baseUrl, 4);
  }

  @Test
  void readsDefiningPagination() throws JsonProcessingException {
    checkPage(baseUrl + "?size=3&page=1", 1);
  }
  
  void checkPage(String url, int expectedSize) throws JsonProcessingException {
    var node = getResultsFor(url);
    assertEquals(expectedSize, node.get("content").size());
  }
  
  JsonNode getResultsFor(String url) throws JsonProcessingException {
    var result = restTemplate.getForEntity(url, String.class);
    assertEquals(HttpStatus.OK, result.getStatusCode());
    
    return new ObjectMapper().readTree(result.getBody());
  }
  
  @Test
  void readsSpecific() throws JsonProcessingException {
    var node = getResultsFor("http://localhost:" + port + "/products/3");
    assertEquals("Grace", node.get("name").asText());
  }
}
