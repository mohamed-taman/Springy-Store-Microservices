package com.siriusxi.ms.store.revs;

import com.siriusxi.ms.store.api.core.review.dto.Review;
import com.siriusxi.ms.store.revs.persistence.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest(webEnvironment=RANDOM_PORT, properties = {
        "spring.datasource.url=jdbc:h2:mem:review-db"})
class ReviewServiceApplicationTests {

    private final String BASE_URI = "/reviews";

    @Autowired
    private WebTestClient client;

    @Autowired
    private ReviewRepository repository;


    @BeforeEach
    public void setupDb() {
        repository.deleteAll();
    }

    @Test
    public void getReviewsByProductId() {

        int productId = 1;

        assertEquals(0, repository.findByProductId(productId).size());

        postAndVerifyReview(productId, 1, OK);
        postAndVerifyReview(productId, 2, OK);
        postAndVerifyReview(productId, 3, OK);

        assertEquals(3, repository.findByProductId(productId).size());

        getAndVerifyReviewsByProductId(productId, OK)
                .jsonPath("$.length()").isEqualTo(3)
                .jsonPath("$[2].productId").isEqualTo(productId)
                .jsonPath("$[2].reviewId").isEqualTo(3);
    }

    @Test
    public void duplicateError() {

        int productId = 1;
        int reviewId = 1;

        assertEquals(0, repository.count());

        postAndVerifyReview(productId, reviewId, OK)
                .jsonPath("$.productId").isEqualTo(productId)
                .jsonPath("$.reviewId").isEqualTo(reviewId);

        assertEquals(1, repository.count());

        postAndVerifyReview(productId, reviewId, UNPROCESSABLE_ENTITY)
                .jsonPath("$.path").isEqualTo(BASE_URI)
                .jsonPath("$.message").isEqualTo("Duplicate key, Product Id: 1, Review Id:1");

        assertEquals(1, repository.count());
    }

    @Test
    public void deleteReviews() {

        int productId = 1;
        int recommendationId = 1;

        postAndVerifyReview(productId, recommendationId, OK);
        assertEquals(1, repository.findByProductId(productId).size());

        deleteAndVerifyReviewsByProductId(productId);
        assertEquals(0, repository.findByProductId(productId).size());

        deleteAndVerifyReviewsByProductId(productId);
    }

    @Test
    public void getReviewsMissingParameter() {

        getAndVerifyReviewsByProductId("", BAD_REQUEST)
                .jsonPath("$.path").isEqualTo(BASE_URI)
                .jsonPath("$.message").isEqualTo("Required int parameter 'productId' is not present");
    }

    @Test
    public void getReviewsInvalidParameter() {

        getAndVerifyReviewsByProductId("?productId=no-integer", BAD_REQUEST)
                .jsonPath("$.path").isEqualTo(BASE_URI)
                .jsonPath("$.message").isEqualTo("Type mismatch.");
    }

    @Test
    public void getReviewsNotFound() {

        getAndVerifyReviewsByProductId("?productId=213", OK)
                .jsonPath("$.length()").isEqualTo(0);
    }

    @Test
    public void getReviewsInvalidParameterNegativeValue() {

        int productIdInvalid = -1;

        getAndVerifyReviewsByProductId("?productId=" + productIdInvalid,
                UNPROCESSABLE_ENTITY)
                .jsonPath("$.path").isEqualTo(BASE_URI)
                .jsonPath("$.message").isEqualTo("Invalid productId: " + productIdInvalid);
    }

    private WebTestClient.BodyContentSpec getAndVerifyReviewsByProductId(int productId,
                                                                         HttpStatus expectedStatus) {
        return getAndVerifyReviewsByProductId("?productId=" + productId, expectedStatus);
    }

    private WebTestClient.BodyContentSpec getAndVerifyReviewsByProductId(String productIdQuery,
                                                                         HttpStatus expectedStatus) {
        return client.get()
                .uri(BASE_URI + productIdQuery)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus)
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody();
    }

    private WebTestClient.BodyContentSpec postAndVerifyReview(int productId,
                                                              int reviewId,
                                                              HttpStatus expectedStatus) {
        Review review = new Review(productId, reviewId, "Author " + reviewId,
                "Subject " + reviewId, "Content " + reviewId, "SA");
        return client.post()
                .uri(BASE_URI)
                .body(Mono.just(review), Review.class)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus)
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody();
    }

    private void deleteAndVerifyReviewsByProductId(int productId) {
         client.delete()
                .uri(BASE_URI + "?productId=" + productId)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(OK)
                .expectBody();
    }
}