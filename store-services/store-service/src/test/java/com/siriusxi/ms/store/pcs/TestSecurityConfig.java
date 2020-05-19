package com.siriusxi.ms.store.pcs;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * The use of OAuth has been disabled when running Spring-based integration tests.
 * To prevent the OAuth machinery from kicking in when we are running integration tests.
 *
 * @author Mohamed Taman
 * @since v5.0, codename: Protector
 * @version v1.0
 */
@TestConfiguration
public class TestSecurityConfig {

  @Bean
  public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
    return http
            .csrf()
              .disable()
            .authorizeExchange()
              .anyExchange().permitAll()
            .and()
            .build();
  }
}
