package com.siriusxi.ms.store.ps;

import com.siriusxi.ms.store.api.core.product.dto.Product;
import com.siriusxi.ms.store.api.event.Event;
import com.siriusxi.ms.store.ps.persistence.ProductRepository;
import com.siriusxi.ms.store.util.exceptions.InvalidInputException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.http.HttpStatus;
import org.springframework.integration.channel.AbstractMessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.siriusxi.ms.store.api.event.Event.Type.CREATE;
import static com.siriusxi.ms.store.api.event.Event.Type.DELETE;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest(
    webEnvironment = RANDOM_PORT,
    properties = {
            "spring.data.mongodb.port: 0",
            "eureka.client.enabled: false",
            "spring.cloud.config.enabled: false",
            "spring.sleuth.enabled: false",
            "spring.data.mongodb.auto-index-creation: true",
            "app.database.host: localhost",
            "server.error.include-message: always"})
class ProductServiceApplicationTests {

  private final String BASE_URI = "/products/";

  @Autowired
  private WebTestClient client;

  @Autowired
  private ProductRepository repository;

  @Autowired
  private Sink channels;

  private AbstractMessageChannel input = null;

  @BeforeEach
  public void setupDb() {
    input = (AbstractMessageChannel) channels.input();
    repository.deleteAll().block();
  }

  @Test
  public void getProductById() {

    int productId = 1;

    assertNull(repository.findByProductId(productId).block());
    assertEquals(0, repository.count().block());

    sendCreateProductEvent(productId);

    assertNotNull(repository.findByProductId(productId).block());
    assertEquals(1, repository.count().block());

    getAndVerifyProduct(productId, OK)
            .jsonPath("$.productId").isEqualTo(productId);
  }

  @Test
  public void duplicateError() {

    int productId = 1;

    assertNull(repository.findByProductId(productId).block());

    sendCreateProductEvent(productId);

    assertNotNull(repository.findByProductId(productId).block());

    try {
      sendCreateProductEvent(productId);
      fail("Expected a MessagingException here!");
    } catch (MessagingException me) {
      if (me.getCause() instanceof InvalidInputException iie){
        assertEquals("Duplicate key, Product Id: ".concat(String.valueOf(productId)), iie.getMessage());
      } else {
        fail("Expected a InvalidInputException as the root cause!");
      }
    }
  }

  @Test
  public void deleteProduct() {

    int productId = 1;

    sendCreateProductEvent(productId);
    assertNotNull(repository.findByProductId(productId).block());

    sendDeleteProductEvent(productId);
    assertNull(repository.findByProductId(productId).block());
  }

  @Test
  public void getProductInvalidParameterString() {
    var uri = BASE_URI.concat("no-integer");
    getAndVerifyProduct(uri, BAD_REQUEST)
        .jsonPath("$.path").isEqualTo(uri)
        .jsonPath("$.message").isEqualTo("Type mismatch.");
  }

  @Test
  public void getProductNotFound() {

    int productIdNotFound = 13;

    getAndVerifyProduct(productIdNotFound, NOT_FOUND)
        .jsonPath("$.path").isEqualTo(BASE_URI.concat(String.valueOf(productIdNotFound)))
        .jsonPath("$.message")
            .isEqualTo("No product found for productId: ".concat(String.valueOf(productIdNotFound)));
  }

  @Test
  public void getProductInvalidParameterNegativeValue() {

    int productIdInvalid = -1;

    getAndVerifyProduct(productIdInvalid, UNPROCESSABLE_ENTITY)
        .jsonPath("$.path").isEqualTo(BASE_URI.concat(String.valueOf(productIdInvalid)))
        .jsonPath("$.message").isEqualTo("Invalid productId: ".concat(String.valueOf(productIdInvalid)));
  }

  private WebTestClient.BodyContentSpec getAndVerifyProduct(
      int productId, HttpStatus expectedStatus) {
    return getAndVerifyProduct(BASE_URI.concat(String.valueOf(productId)), expectedStatus);
  }

  private WebTestClient.BodyContentSpec getAndVerifyProduct(
      String productIdPath, HttpStatus expectedStatus) {
    return client
        .get()
        .uri(productIdPath)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus().isEqualTo(expectedStatus)
        .expectHeader().contentType(APPLICATION_JSON)
        .expectBody();
  }

  private void sendCreateProductEvent(int productId) {
    var product = new Product(productId,
            "Name ".concat(String.valueOf(productId)),
            productId,
            "SA");
    Event<Integer, Product> event = new Event<>(CREATE, productId, product);
    input.send(new GenericMessage<>(event));
  }

  private void sendDeleteProductEvent(int productId) {
    Event<Integer, Product> event = new Event<>(DELETE, productId, null);
    input.send(new GenericMessage<>(event));
  }
}
