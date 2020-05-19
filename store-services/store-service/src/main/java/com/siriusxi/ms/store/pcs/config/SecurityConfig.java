package com.siriusxi.ms.store.pcs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import static org.springframework.http.HttpMethod.*;

/**
 * This SecurityConfig class allows
 *
 * <pre>StoreServiceApplication</pre>
 *
 * to act as oauth2 Resource Server.
 *
  * @author Mohamed Taman
  * @since v5.0, codename: Protector
  * @version v1.0
 */
@EnableWebFluxSecurity
public class SecurityConfig {

  @Bean
  SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
    var baseUri = "/store/api/v1/products/**";
    /*
     By convention, OAuth 2.0 scopes should be prefixed with SCOPE_
     when checked for authority using Spring Security.
    */
    http.authorizeExchange()
          .pathMatchers("/actuator/**").permitAll()
          .pathMatchers(POST, baseUri).hasAuthority("SCOPE_product:write")
          .pathMatchers(DELETE, baseUri).hasAuthority("SCOPE_product:write")
          .pathMatchers(GET, baseUri).hasAuthority("SCOPE_product:read")
        // Ensures that the user is authenticated before being allowed access to all other URLs
        .anyExchange().authenticated()
        .and()
        /*
         1. specifies that authentication and authorization will be based on
         a JWT-encoded OAuth 2.0 access token

         2. The endpoint of the authorization server's jwk-set endpoint has been
         registered in the configuration file, store.yml
        */
        .oauth2ResourceServer()
          .jwt();

    return http.build();
  }
}
