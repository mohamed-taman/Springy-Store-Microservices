package com.siriusxi.cloud.infra.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * This SecurityConfig class allows
 * <pre>StoreServiceApplication</pre> to act as oauth2 Resource Server.
 *
 * @author Mohamed Taman
 * @since v5.0, codename: Protector
 * @version v1.0
 */
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http)  {
    http.csrf().disable()
        .authorizeExchange()
            .pathMatchers("/headerrouting/**").permitAll()
            .pathMatchers("/actuator/**").permitAll()
            .pathMatchers("/eureka/**").permitAll()
            .pathMatchers("/oauth/**").permitAll()
            .anyExchange().authenticated()
        .and()
            .oauth2ResourceServer()
            .jwt();

    return http.build();
    }

}
