package com.siriusxi.ms.store.pcs;

import com.siriusxi.ms.store.api.composite.dto.ProductAggregate;
import com.siriusxi.ms.store.api.composite.dto.RecommendationSummary;
import com.siriusxi.ms.store.api.composite.dto.ReviewSummary;
import com.siriusxi.ms.store.api.core.product.dto.Product;
import com.siriusxi.ms.store.api.core.recommendation.dto.Recommendation;
import com.siriusxi.ms.store.api.core.review.dto.Review;
import com.siriusxi.ms.store.api.event.Event;
import com.siriusxi.ms.store.pcs.integration.StoreIntegration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.BlockingQueue;

import static com.siriusxi.ms.store.api.event.Event.Type.CREATE;
import static com.siriusxi.ms.store.api.event.Event.Type.DELETE;
import static com.siriusxi.ms.store.pcs.IsSameEvent.sameEventExceptCreatedAt;
import static java.lang.String.valueOf;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.cloud.stream.test.matcher.MessageQueueMatcher.receivesPayloadThat;
import static org.springframework.http.HttpStatus.OK;

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
            "spring.cloud.config.enabled: false"
    })
class MessagingTests {

  public static final String BASE_URL = "/store/api/v1/products/";

  BlockingQueue<Message<?>> queueProducts = null;
  BlockingQueue<Message<?>> queueRecommendations = null;
  BlockingQueue<Message<?>> queueReviews = null;

  @Autowired private WebTestClient client;
  @Autowired private StoreIntegration.MessageSources channels;
  @Autowired private MessageCollector collector;

  @BeforeEach
  public void setUp() {
    queueProducts = getQueue(channels.outputProducts());
    queueRecommendations = getQueue(channels.outputRecommendations());
    queueReviews = getQueue(channels.outputReviews());
  }

  @Test
  public void createCompositeProduct1() {

    ProductAggregate composite = new ProductAggregate(1, "name", 1, null, null, null);
    postAndVerifyProduct(composite);

    // Assert one expected new product events queued up
    assertEquals(1, queueProducts.size());

    Event<Integer, Product> expectedEvent =
        new Event<>(
            CREATE,
            composite.productId(),
            new Product(composite.productId(), composite.name(), composite.weight(), null));
    assertThat(queueProducts, is(receivesPayloadThat(sameEventExceptCreatedAt(expectedEvent))));

    // Assert none recommendations and review events
    assertEquals(0, queueRecommendations.size());
    assertEquals(0, queueReviews.size());
  }

  @Test
  public void createCompositeProduct2() {

    ProductAggregate composite =
        new ProductAggregate(
            1,
            "name",
            1,
            singletonList(new RecommendationSummary(1, "a", 1, "c")),
            singletonList(new ReviewSummary(1, "a", "s", "c")),
            null);

    postAndVerifyProduct(composite);

    // Assert one create product event queued up
    assertEquals(1, queueProducts.size());

    Event<Integer, Product> expectedProductEvent =
        new Event<>(
            CREATE,
            composite.productId(),
            new Product(composite.productId(), composite.name(), composite.weight(), null));
    assertThat(queueProducts, receivesPayloadThat(sameEventExceptCreatedAt(expectedProductEvent)));

    // Assert one create recommendation event queued up
    assertEquals(1, queueRecommendations.size());

    RecommendationSummary rec = composite.recommendations().get(0);
    Event<Integer, Recommendation> expectedRecommendationEvent =
        new Event<>(
            CREATE,
            composite.productId(),
            new Recommendation(
                composite.productId(),
                rec.recommendationId(),
                rec.author(),
                rec.rate(),
                rec.content(),
                null));
    assertThat(
        queueRecommendations,
        receivesPayloadThat(sameEventExceptCreatedAt(expectedRecommendationEvent)));

    // Assert one create review event queued up
    assertEquals(1, queueReviews.size());

    ReviewSummary rev = composite.reviews().get(0);
    Event<Integer, Review> expectedReviewEvent =
        new Event<>(
            CREATE,
            composite.productId(),
            new Review(
                composite.productId(),
                rev.reviewId(),
                rev.author(),
                rev.subject(),
                rev.content(),
                null));

    assertThat(queueReviews, receivesPayloadThat(sameEventExceptCreatedAt(expectedReviewEvent)));
  }

  @Test
  public void deleteCompositeProduct() {

    deleteAndVerifyProduct(1);

    // Assert one delete product event queued up
    assertEquals(1, queueProducts.size());

    Event<Integer, Product> expectedEvent = new Event<>(DELETE, 1, null);

    assertThat(queueProducts, is(receivesPayloadThat(sameEventExceptCreatedAt(expectedEvent))));

    // Assert one delete recommendation event queued up
    assertEquals(1, queueRecommendations.size());

    Event<Integer, Product> expectedRecommendationEvent = new Event<>(DELETE, 1, null);
    assertThat(
        queueRecommendations,
        receivesPayloadThat(sameEventExceptCreatedAt(expectedRecommendationEvent)));

    // Assert one delete review event queued up
    assertEquals(1, queueReviews.size());

    Event<Integer, Product> expectedReviewEvent = new Event<>(DELETE, 1, null);
    assertThat(queueReviews, receivesPayloadThat(sameEventExceptCreatedAt(expectedReviewEvent)));
  }

  private BlockingQueue<Message<?>> getQueue(MessageChannel messageChannel) {
    return collector.forChannel(messageChannel);
  }

  private void postAndVerifyProduct(ProductAggregate compositeProduct) {
    client
        .post()
        .uri(BASE_URL)
        .body(Mono.just(compositeProduct), ProductAggregate.class)
        .exchange()
        .expectStatus()
        .isEqualTo(OK);
  }

  private void deleteAndVerifyProduct(int productId) {
    client
        .delete()
        .uri(BASE_URL.concat(valueOf(productId)))
        .exchange()
        .expectStatus()
        .isEqualTo(OK);
  }
}
