package com.siriusxi.ms.store.api.core.review;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface ReviewService {

    /**
     * Sample usage: curl $HOST:$PORT/review?productId=1
     *
     * @param productId that you are looking for its reviews.
     * @return list of reviews for this product,
     *         or empty list if there are no reviews.
     */
    @GetMapping(
            value    = "/review",
            produces = "application/json")
    List<Review> getReviews(@RequestParam(value = "productId") int productId);
}
