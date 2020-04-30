package com.siriusxi.cloud.infra.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(
    webEnvironment = RANDOM_PORT,
    properties = {"eureka.client.enabled: false"})
// TODO add test cases https://spring.io/guides/gs/gateway/
class EdgeServerApplicationTests {

  @Test
  void contextLoads() {
    assertTrue(true);
  }
}
