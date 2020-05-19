package com.siriusxi.cloud.infra.auth.server;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for {@link AuthorizationServer}
 *
 * @author Mohamed Taman
 * @since 5.0
 */
@SpringBootTest(properties = {
        "eureka.client.enabled: false",
        "spring.cloud.config.enabled: false"})
@AutoConfigureMockMvc
class AuthorizationServerTests {
  @Autowired MockMvc mvc;

  @Test
  public void requestTokenWhenUsingPasswordGrantTypeThenOk() throws Exception {

    this.mvc
        .perform(
            post("/oauth/token")
                .param("grant_type", "password")
                .param("username", "taman")
                .param("password", "password")
                .header("Authorization", "Basic cmVhZGVyOnNlY3JldA=="))
        .andExpect(status().isOk());
  }

  @Test
  public void requestJwkSetWhenUsingDefaultsThenOk() throws Exception {

    this.mvc
            .perform(get("/.well-known/jwks.json"))
            .andExpect(status().isOk());
  }
}
