package com.siriusxi.ms.store.api.core.review;

import com.siriusxi.ms.store.api.core.review.dto.Review;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Interface <code>ReviewEndpoint</code> is a higher level Interface to define <strong>Review
 * Service</strong> endpoint APIs, that follow <code>ReviewService</code> interface. And to be
 * implemented by service controllers.
 *
 * @see ReviewService
 * @author mohamed.taman
 * @version v4.0
 * @since v3.0 codename Storm
 */
@RequestMapping("reviews")
public interface ReviewEndpoint extends ReviewService {

  /**
   * Sample usage:
   *
   * <p><code>curl $HOST:$PORT/reviews?productId=1</code>
   *
   * @param productId that you are looking for its reviews.
   * @return list of reviews for this product, or empty list if there are no reviews.
   * @since v3.0 codename Storm
   */
  @GetMapping(produces = APPLICATION_JSON_VALUE)
  @Override
  Flux<Review> getReviews(@RequestParam("productId") int productId);
}
