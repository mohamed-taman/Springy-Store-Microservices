package com.siriusxi.ms.store.api.core.recommendation;

import com.siriusxi.ms.store.api.core.recommendation.dto.Recommendation;
import reactor.core.publisher.Flux;

/**
 * Interface that define the general service contract (methods) for the Recommendation
 *
 * <ol>
 *   <li>Service and,
 *   <li>Controller interfaces.
 * </ol>
 *
 * @author mohamed.taman
 * @version v0.2
 * @since v0.1
 */
public interface RecommendationService {
  /**
   * Get all recommendations for specific product by product id. It is a Non-Blocking API.
   *
   * @param productId that you are looking for its recommendations.
   * @return list of product recommendations, or empty list if there are no recommendations.
   * @since v0.1
   */
  Flux<Recommendation> getRecommendations(int productId);

  /**
   * Create a new recommendation for a product.
   *
   * @param body the recommendation to add.
   * @return currently created recommendation.
   * @since v0.1
   */
  default Recommendation createRecommendation(Recommendation body) {
    return null;
  }

  /**
   * Delete all product recommendations.
   *
   * @param productId to delete recommendations for.
   * @since v0.1
   */
  default void deleteRecommendations(int productId) {}
}
