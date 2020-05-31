package com.siriusxi.cloud.infra.eds;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(
    webEnvironment = RANDOM_PORT,
    properties = {
      "spring.cloud.config.enabled: false",
      "eureka.client.register-with-eureka: false",
      "eureka.client.fetch-registry: false",
      "management.health.rabbit.enabled=false",
      "app.eureka.user: mt",
      "app.eureka.pass: p"
    })
class EurekaDiscoveryServerTests {

  @Value("${app.eureka.user}")
  private String username;

  @Value("${app.eureka.pass}")
  private String password;

  private TestRestTemplate testRestTemplate;

  @Autowired
  public void setTestRestTemplate(TestRestTemplate testRestTemplate) {
    this.testRestTemplate = testRestTemplate.withBasicAuth(username, password);
  }

  @Test
  public void catalogLoads() {

    String expectedResponseBody =
        "{\"applications\":{\"versions__delta\":\"1\","
            + "\"apps__hashcode\":\"\",\"application\":[]}}";
    ResponseEntity<String> entity = testRestTemplate.getForEntity("/eureka/apps", String.class);
    assertEquals(HttpStatus.OK, entity.getStatusCode());
    assertEquals(expectedResponseBody, entity.getBody());
  }

  @Test
  public void healthy() {
    String expectedResponseBody = "{\"status\":\"UP\"}";
    ResponseEntity<String> entity = testRestTemplate.getForEntity("/actuator/health", String.class);
    assertEquals(HttpStatus.OK, entity.getStatusCode());
    assertEquals(expectedResponseBody, entity.getBody());
  }
}
