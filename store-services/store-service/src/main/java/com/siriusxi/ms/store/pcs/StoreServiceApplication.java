package com.siriusxi.ms.store.pcs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebFlux;

@SpringBootApplication
@EnableSwagger2WebFlux // Starting point for initiating SpringFox
@ComponentScan("com.siriusxi.ms.store")
public class StoreServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(StoreServiceApplication.class, args);
  }
}
