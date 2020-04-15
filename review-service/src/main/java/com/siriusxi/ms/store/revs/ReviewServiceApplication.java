package com.siriusxi.ms.store.revs;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import static org.springframework.boot.SpringApplication.run;

@SpringBootApplication
@ComponentScan("com.siriusxi.ms.store")
@Log4j2
public class ReviewServiceApplication {

  public static void main(String[] args) {

    ConfigurableApplicationContext ctx = run(ReviewServiceApplication.class, args);

    String mysqlUri = ctx.getEnvironment().getProperty("spring.datasource.url");
    log.info("Connected to MySQL: " + mysqlUri);
  }
}
