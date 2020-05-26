package com.siriusxi.ms.store.pcs;

import com.siriusxi.ms.store.api.core.product.dto.Product;
import com.siriusxi.ms.store.api.core.recommendation.dto.Recommendation;
import com.siriusxi.ms.store.api.core.review.dto.Review;
import com.siriusxi.ms.store.pcs.integration.StoreIntegration;
import com.siriusxi.ms.store.util.exceptions.InvalidInputException;
import com.siriusxi.ms.store.util.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.BodyContentSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static java.util.Collections.singletonList;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest(
    webEnvironment = RANDOM_PORT,
    /*
     In this Spring integration test class,
     configure TestSecurityConfig to override the
     existing security configuration.
    */
    classes = {StoreServiceApplication.class, TestSecurityConfig.class},
    properties = {
      "spring.main.allow-bean-definition-overriding: true",
      "eureka.client.enabled: false",
      "spring.cloud.config.enabled: false",
      "server.error.include-message: always"
    })
class StoreServiceApplicationTests {

  public static final String BASE_URL = "/store/api/v1/products/";
  private static final int PRODUCT_ID_OK = 1;
  private static final int PRODUCT_ID_NOT_FOUND = 2;
  private static final int PRODUCT_ID_INVALID = 3;

  @Autowired private WebTestClient client;

  @MockBean private StoreIntegration storeIntegration;

  @BeforeEach
  void setUp() {

    when(storeIntegration.getProduct(eq(PRODUCT_ID_OK), anyInt(), anyInt()))
        .thenReturn(Mono.just(new Product(PRODUCT_ID_OK, "name", 1, "mock-address")));

    when(storeIntegration.getRecommendations(PRODUCT_ID_OK))
        .thenReturn(
            Flux.fromIterable(
                singletonList(
                    new Recommendation(PRODUCT_ID_OK, 1, "author", 1, "content", "mock address"))));

    when(storeIntegration.getReviews(PRODUCT_ID_OK))
        .thenReturn(
            Flux.fromIterable(
                singletonList(
                    new Review(PRODUCT_ID_OK, 1, "author", "subject", "content", "mock address"))));

    when(storeIntegration.getProduct(eq(PRODUCT_ID_NOT_FOUND), anyInt(), anyInt()))
        .thenThrow(new NotFoundException("NOT FOUND: " + PRODUCT_ID_NOT_FOUND));

    when(storeIntegration.getProduct(eq(PRODUCT_ID_INVALID), anyInt(), anyInt()))
        .thenThrow(new InvalidInputException("INVALID: " + PRODUCT_ID_INVALID));
  }

  @Test
  public void getProductById() {

    getAndVerifyProduct(PRODUCT_ID_OK, OK)
        .jsonPath("$.productId")
        .isEqualTo(PRODUCT_ID_OK)
        .jsonPath("$.recommendations.length()")
        .isEqualTo(1)
        .jsonPath("$.reviews.length()")
        .isEqualTo(1);
  }

  @Test
  public void getProductNotFound() {

    getAndVerifyProduct(PRODUCT_ID_NOT_FOUND, NOT_FOUND)
        .jsonPath("$.path")
        .isEqualTo(BASE_URL + PRODUCT_ID_NOT_FOUND)
        .jsonPath("$.message")
        .isEqualTo("NOT FOUND: " + PRODUCT_ID_NOT_FOUND);
  }

  @Test
  public void getProductInvalidInput() {

    getAndVerifyProduct(PRODUCT_ID_INVALID, UNPROCESSABLE_ENTITY)
        .jsonPath("$.path")
        .isEqualTo(BASE_URL + PRODUCT_ID_INVALID)
        .jsonPath("$.message")
        .isEqualTo("INVALID: " + PRODUCT_ID_INVALID);
  }

  private BodyContentSpec getAndVerifyProduct(int productId, HttpStatus expectedStatus) {
    return client
        .get()
        .uri(BASE_URL + productId)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isEqualTo(expectedStatus)
        .expectHeader()
        .contentType(APPLICATION_JSON)
        .expectBody();
  }
}
