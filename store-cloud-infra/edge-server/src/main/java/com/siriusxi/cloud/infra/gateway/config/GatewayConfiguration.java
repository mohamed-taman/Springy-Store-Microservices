package com.siriusxi.cloud.infra.gateway.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.CompositeReactiveHealthContributor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.ReactiveHealthContributor;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

// TODO add swagger API
@Configuration
@Log4j2
public class GatewayConfiguration {

  private final WebClient.Builder webClientBuilder;

  private WebClient webClient;

  @Autowired
  public GatewayConfiguration(WebClient.Builder webClientBuilder) {
    this.webClientBuilder = webClientBuilder;
  }

  /**
   * This method is to check all the services health status, and Gateway service health will be only
   * in up health state if and only if all of the core services and dependencies are up and running.
   *
   * @return ReactiveHealthContributor information about all the core microservices.
   */
  @Bean(name = "Core Microservices")
  ReactiveHealthContributor coreServices() {

    ReactiveHealthIndicator productHealthIndicator = () -> getServicesHealth("http://product");
    ReactiveHealthIndicator recommendationHealthIndicator =
        () -> getServicesHealth("http://recommendation");
    ReactiveHealthIndicator reviewHealthIndicator = () -> getServicesHealth("http://review");
    ReactiveHealthIndicator storeHealthIndicator = () -> getServicesHealth("http://store");
    ReactiveHealthIndicator authHealthIndicator = () -> getServicesHealth("http://auth-server");

    Map<String, ReactiveHealthContributor> healthIndicators =
        Map.of(
            "Product Service", productHealthIndicator,
            "Recommendation Service", recommendationHealthIndicator,
            "Review Service", reviewHealthIndicator,
            "Store Service", storeHealthIndicator,
            "Authorization Server", authHealthIndicator);

    return CompositeReactiveHealthContributor.fromMap(healthIndicators);
  }

  private Mono<Health> getServicesHealth(String url) {
    url += "/actuator/health";

    log.debug("Will call the Health API on URL: {}", url);

    return getWebClient()
            .get().uri(url)
            .retrieve().bodyToMono(String.class)
            .map(s -> new Health.Builder()
                    .up()
                    .build())
            .onErrorResume(ex -> Mono.just(new Health.Builder()
                    .down(ex)
                    .build()))
            .log();
  }

  private WebClient getWebClient() {
    if (webClient == null) {
      webClient = webClientBuilder.build();
    }
    return webClient;
  }
}
