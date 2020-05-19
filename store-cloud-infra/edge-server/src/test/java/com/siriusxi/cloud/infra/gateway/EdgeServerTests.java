package com.siriusxi.cloud.infra.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(
    webEnvironment = RANDOM_PORT,
    properties = {
            "eureka.client.enabled: false",
            "spring.cloud.config.enabled: false"})
class EdgeServerTests {

  @Test
  void contextLoads() {
    assertTrue(true);
  }
}
