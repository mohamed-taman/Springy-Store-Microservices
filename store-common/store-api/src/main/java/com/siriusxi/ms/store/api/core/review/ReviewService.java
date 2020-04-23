package com.siriusxi.ms.store.api.core.review;

import com.siriusxi.ms.store.api.core.review.dto.Review;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * Interface that define the general service contract (methods) for the Review
 *
 * <ol>
 *   <li>Service and,
 *   <li>Controller interfaces.
 * </ol>
 *
 * @author mohamed.taman
 * @version v4.0
 * @since v0.1
 */
public interface ReviewService {

  /**
   * Get all reviews for specific product by product id.
   * It is a Non-Blocking API.
   *
   * @param productId that you are looking for its reviews.
   * @return list of reviews for this product, or empty list if there are no reviews.
   */
  Flux<Review> getReviews(int productId);

  /**
   * Create a new review for a product.
   *
   * @param body review to be created.
   * @return just created review.
   */
  default Review createReview(Review body){return null;}

  /**
   * Delete all product reviews.
   *
   * @param productId to delete its reviews.
   */
  default void deleteReviews(int productId){}
}
