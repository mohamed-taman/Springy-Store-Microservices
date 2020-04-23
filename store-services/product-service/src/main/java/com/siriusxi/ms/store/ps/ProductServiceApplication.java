package com.siriusxi.ms.store.ps;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.siriusxi.ms.store")
@Log4j2
public class ProductServiceApplication {

  public static void main(String[] args) {
    ConfigurableApplicationContext ctx =
        SpringApplication.run(ProductServiceApplication.class, args);

    var mongoDbHost = ctx.getEnvironment().getProperty("spring.data.mongodb.host");
    var mongoDbPort = ctx.getEnvironment().getProperty("spring.data.mongodb.port");
    log.info("Connected to MongoDb: " + mongoDbHost + ":" + mongoDbPort);
  }
}
