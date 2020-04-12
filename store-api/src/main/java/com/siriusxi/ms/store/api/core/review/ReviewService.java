package com.siriusxi.ms.store.api.core.review;

import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

//@RequestMapping("reviews")
public interface ReviewService {

    /**
     * Sample usage:
     *
     * curl -X POST $HOST:$PORT/reviews \
     *   -H "Content-Type: application/json" --data \
     *   '{"productId":123,"reviewId":456,"author":"me","subject":"yada, yada, yada",
     *     "content":"yada, yada, yada"}'
     *
     * @param body review to be created.
     * @return just created review.
     */
    @PostMapping(value = "reviews",
            produces = APPLICATION_JSON_VALUE,
            consumes = APPLICATION_JSON_VALUE)
    Review createReview(@RequestBody Review body);

    /**
     * Sample usage: curl $HOST:$PORT/reviews?productId=1
     *
     * @param productId that you are looking for its reviews.
     * @return list of reviews for this product,
     * or empty list if there are no reviews.
     */
    @GetMapping(value = "reviews",
            produces = APPLICATION_JSON_VALUE)
    List<Review> getReviews(@RequestParam("productId") int productId);


    /**
     * Sample usage:
     *
     * curl -X DELETE $HOST:$PORT/review?productId=1
     *
     * @param productId to delete its reviews.
     */
    @DeleteMapping(value = "reviews")
    void deleteReviews(@RequestParam("productId")  int productId);
}
