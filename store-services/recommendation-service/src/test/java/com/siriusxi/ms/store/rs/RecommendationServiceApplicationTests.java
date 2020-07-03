package com.siriusxi.ms.store.rs;

import com.siriusxi.ms.store.api.core.recommendation.dto.Recommendation;
import com.siriusxi.ms.store.api.event.Event;
import com.siriusxi.ms.store.rs.persistence.RecommendationRepository;
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
import org.springframework.test.web.reactive.server.WebTestClient.BodyContentSpec;
import reactor.core.publisher.Mono;

import static com.siriusxi.ms.store.api.event.Event.Type.CREATE;
import static com.siriusxi.ms.store.api.event.Event.Type.DELETE;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
class RecommendationServiceApplicationTests {

  private final String BASE_URI = "/recommendations";

  @Autowired private WebTestClient client;

  @Autowired private RecommendationRepository repository;

  @Autowired
  private Sink channels;

  private AbstractMessageChannel input = null;

  @BeforeEach
  public void setupDb() {
    input = (AbstractMessageChannel) channels.input();
    repository.deleteAll().block();
  }

  @Test
  public void getRecommendationsByProductId() {

    int productId = 1;

    sendCreateRecommendationEvent(productId, 1);
    sendCreateRecommendationEvent(productId, 2);
    sendCreateRecommendationEvent(productId, 3);

    assertEquals(3, repository.findByProductId(productId).count().block());

    getAndVerifyRecommendationsByProductId(productId)
        .jsonPath("$.length()").isEqualTo(3)
        .jsonPath("$[2].productId").isEqualTo(productId)
        .jsonPath("$[2].recommendationId").isEqualTo(3);
  }

  @Test
  public void duplicateError() {

    int productId = 1;
    int recommendationId = 1;

    sendCreateRecommendationEvent(productId, recommendationId);

    assertEquals(1, repository.count().block());

    try {
      sendCreateRecommendationEvent(productId, recommendationId);
      fail("Expected a MessagingException here!");
    } catch (MessagingException me) {
      if (me.getCause() instanceof InvalidInputException iie)	{
        assertEquals("Duplicate key, Product Id: 1, Recommendation Id:1", iie.getMessage());
      } else {
        fail("Expected a InvalidInputException as the root cause!");
      }
    }

    assertEquals(1, repository.count().block());
  }

  @Test
  public void deleteRecommendations() {

    int productId = 1;
    int recommendationId = 1;

    sendCreateRecommendationEvent(productId, recommendationId);
    assertEquals(1, repository.findByProductId(productId).count().block());

    sendDeleteRecommendationEvent(productId);
    assertEquals(0, repository.findByProductId(productId).count().block());
  }

  @Test
  public void getRecommendationsMissingParameter() {

    getAndVerifyRecommendationsByProductId("", BAD_REQUEST)
        .jsonPath("$.path").isEqualTo(BASE_URI)
        .jsonPath("$.message")
            .isEqualTo("Required int parameter 'productId' is not present");
  }

  @Test
  public void getRecommendationsInvalidParameter() {

    getAndVerifyRecommendationsByProductId("?productId=no-integer", BAD_REQUEST)
        .jsonPath("$.path").isEqualTo(BASE_URI)
        .jsonPath("$.message").isEqualTo("Type mismatch.");
  }

  @Test
  public void getRecommendationsNotFound() {

    getAndVerifyRecommendationsByProductId("?productId=113", OK)
        .jsonPath("$.length()").isEqualTo(0);
  }

  @Test
  public void getRecommendationsInvalidParameterNegativeValue() {

    int productIdInvalid = -1;

    getAndVerifyRecommendationsByProductId("?productId=" + productIdInvalid,
            UNPROCESSABLE_ENTITY)
        .jsonPath("$.path").isEqualTo(BASE_URI)
        .jsonPath("$.message").isEqualTo("Invalid productId: " + productIdInvalid);
  }

  private BodyContentSpec getAndVerifyRecommendationsByProductId(
          int productId) {
    return getAndVerifyRecommendationsByProductId("?productId=" + productId, HttpStatus.OK);
  }

  private BodyContentSpec getAndVerifyRecommendationsByProductId(
      String productIdQuery, HttpStatus expectedStatus) {
    return client
        .get()
        .uri(BASE_URI + productIdQuery)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isEqualTo(expectedStatus)
        .expectHeader()
        .contentType(APPLICATION_JSON)
        .expectBody();
  }

  private void sendCreateRecommendationEvent(int productId, int recommendationId) {
    Recommendation recommendation = new Recommendation(productId, recommendationId, "Author " + recommendationId, recommendationId, "Content " + recommendationId, "SA");
    Event<Integer, Recommendation> event = new Event<>(CREATE, productId, recommendation);
    input.send(new GenericMessage<>(event));
  }

  private void sendDeleteRecommendationEvent(int productId) {
    Event<Integer, Recommendation> event = new Event<>(DELETE, productId, null);
    input.send(new GenericMessage<>(event));
  }
}