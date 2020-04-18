package com.siriusxi.ms.store.pcs;

import com.siriusxi.ms.store.api.composite.dto.ProductAggregate;
import com.siriusxi.ms.store.api.composite.dto.RecommendationSummary;
import com.siriusxi.ms.store.api.composite.dto.ReviewSummary;
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
import reactor.core.publisher.Mono;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class StoreServiceApplicationTests {

  public static final String BASE_URL = "/store/api/v1/products/";
  private static final int PRODUCT_ID_OK = 1;
  private static final int PRODUCT_ID_NOT_FOUND = 2;
  private static final int PRODUCT_ID_INVALID = 3;

  @Autowired
  private WebTestClient client;

  @MockBean
  private StoreIntegration storeIntegration;

  @BeforeEach
  void setUp() {

    when(storeIntegration.getProduct(PRODUCT_ID_OK))
        .thenReturn(new Product(PRODUCT_ID_OK, "name", 1, "mock-address"));

    when(storeIntegration.getRecommendations(PRODUCT_ID_OK))
        .thenReturn(
            singletonList(
                new Recommendation(PRODUCT_ID_OK, 1, "author", 1, "content", "mock address")));

    when(storeIntegration.getReviews(PRODUCT_ID_OK))
        .thenReturn(
            singletonList(
                new Review(PRODUCT_ID_OK, 1, "author", "subject", "content", "mock address")));

    when(storeIntegration.getProduct(PRODUCT_ID_NOT_FOUND))
        .thenThrow(new NotFoundException("NOT FOUND: " + PRODUCT_ID_NOT_FOUND));

    when(storeIntegration.getProduct(PRODUCT_ID_INVALID))
        .thenThrow(new InvalidInputException("INVALID: " + PRODUCT_ID_INVALID));
  }

  @Test
  public void createCompositeProduct1() {

    var compositeProduct = new ProductAggregate(1, "name", 1, null, null, null);

    postAndVerifyProductIsCreated(compositeProduct);
  }

  @Test
  public void createCompositeProduct2() {
    var compositeProduct =
        new ProductAggregate(
            1,
            "name",
            1,
            singletonList(new RecommendationSummary(1, "a", 1, "c")),
            singletonList(new ReviewSummary(1, "a", "s", "c")),
            null);

    postAndVerifyProductIsCreated(compositeProduct);
  }

  @Test
  public void deleteCompositeProduct() {
    var compositeProduct =
        new ProductAggregate(
            1,
            "name",
            1,
            singletonList(new RecommendationSummary(1, "a", 1, "c")),
            singletonList(new ReviewSummary(1, "a", "s", "c")),
            null);

    postAndVerifyProductIsCreated(compositeProduct);

    deleteAndVerifyProductIsDeleted(compositeProduct.getProductId());
    deleteAndVerifyProductIsDeleted(compositeProduct.getProductId());
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

  private void postAndVerifyProductIsCreated(ProductAggregate compositeProduct) {
    client
        .post()
        .uri(BASE_URL)
        .body(Mono.just(compositeProduct), ProductAggregate.class)
        .exchange()
        .expectStatus()
        .isEqualTo(OK);
  }

  private void deleteAndVerifyProductIsDeleted(int productId) {
    client.delete().uri(BASE_URL + productId).exchange().expectStatus().isEqualTo(OK);
  }
}
