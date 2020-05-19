package com.siriusxi.cloud.infra.cs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class CentralizedConfigServer {

  public static void main(String[] args) {
    SpringApplication.run(CentralizedConfigServer.class, args);
  }
}
