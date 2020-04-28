package com.siriusxi.ms.store.pcs.config;

import com.siriusxi.ms.store.pcs.integration.StoreIntegration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.CompositeReactiveHealthContributor;
import org.springframework.boot.actuate.health.ReactiveHealthContributor;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Map;

import static java.util.Collections.emptyList;
import static org.springframework.web.bind.annotation.RequestMethod.*;
import static springfox.documentation.builders.RequestHandlerSelectors.basePackage;
import static springfox.documentation.spi.DocumentationType.SWAGGER_2;

@Configuration
public class StoreServiceConfiguration {

  private final StoreIntegration integration;

  @Value("${api.common.version}")
  private String apiVersion;

  @Value("${api.common.title}")
  private String apiTitle;

  @Value("${api.common.description}")
  private String apiDescription;

  @Value("${api.common.termsOfServiceUrl}")
  private String apiTermsOfServiceUrl;

  @Value("${api.common.license}")
  private String apiLicense;

  @Value("${api.common.licenseUrl}")
  private String apiLicenseUrl;

  @Value("${api.common.contact.name}")
  private String apiContactName;

  @Value("${api.common.contact.url}")
  private String apiContactUrl;

  @Value("${api.common.contact.email}")
  private String apiContactEmail;

  @Autowired
  public StoreServiceConfiguration(StoreIntegration integration) {
    this.integration = integration;
  }

  /**
   * This method is to check all the services health status, and store service health will be only
   * in up health state if and only if all of the core services and dependencies are up and running.
   *
   * @return ReactiveHealthContributor information about all the core microservices.
   */
  @Bean(name = "Core Microservices")
  ReactiveHealthContributor coreServices() {

    ReactiveHealthIndicator productHealthIndicator = integration::getProductHealth;
    ReactiveHealthIndicator recommendationHealthIndicator = integration::getRecommendationHealth;
    ReactiveHealthIndicator reviewHealthIndicator = integration::getReviewHealth;

    Map<String, ReactiveHealthContributor> allIndicators =
        Map.of(
            "Product Service", productHealthIndicator,
            "Recommendation Service", recommendationHealthIndicator,
            "Review Service", reviewHealthIndicator);

    return CompositeReactiveHealthContributor.fromMap(allIndicators);
  }

  /**
   * Will exposed on $HOST:$PORT/swagger-ui.html
   *
   * @return Docket swagger configuration
   */
  @Bean
  public Docket apiDocumentation() {

    return new Docket(SWAGGER_2)
        .select()
        /*
           Using the apis() and paths() methods,
           we can specify where SpringFox shall look for API documentation.
        */
        .apis(basePackage("com.siriusxi.ms.store"))
        .paths(PathSelectors.any())
        .build()
        /*
            Using the globalResponseMessage() method, we ask SpringFox not to add any default HTTP
            response codes to the API documentation, such as 401 and 403,
            which we don't currently use.
        */
        .globalResponseMessage(POST, emptyList())
        .globalResponseMessage(GET, emptyList())
        .globalResponseMessage(DELETE, emptyList())
        /*
            The api* variables that are used to configure the Docket bean with general
            information about the API are initialized from the property file using
            Spring @Value annotations.
        */
        .apiInfo(
            new ApiInfo(
                apiTitle,
                apiDescription,
                apiVersion,
                apiTermsOfServiceUrl,
                new Contact(apiContactName, apiContactUrl, apiContactEmail),
                apiLicense,
                apiLicenseUrl,
                emptyList()));
  }
}
