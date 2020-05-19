package com.siriusxi.cloud.infra.cs.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
    http
        // Disable CRCF to allow POST to /encrypt and /decrypt endpoints
        .csrf()
            .disable()
        .authorizeRequests()
            .anyRequest().authenticated()
            .and()
            .httpBasic();
    }
}
