package io.github.dvplabs.scrud.sample;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

/**
 *
 * @author david
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ModifyOperationsTest {
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
  void createsNew() {
    var payload = ProductDto.builder().name("test-product").build();
    var response = restTemplate.postForEntity(baseUrl, payload, String.class);
    assertEquals(201, response.getStatusCodeValue());
  }
  
  @Test
  void update() {
    var payload = ProductDto.builder().name("Updated").build();
    var response = restTemplate.exchange(baseUrl + "/1", HttpMethod.PUT, new HttpEntity(payload), String.class);
    assertEquals(200, response.getStatusCodeValue());
  }
  
  @Test
  void delete() {
    var response = restTemplate.exchange(baseUrl + "/2", HttpMethod.DELETE, null, String.class);
    assertEquals(200, response.getStatusCodeValue());
  }
  
}
