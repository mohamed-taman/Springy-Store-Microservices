package com.siriusxi.ms.store.rs.api;

import com.siriusxi.ms.store.api.core.recommendation.RecommendationEndpoint;
import com.siriusxi.ms.store.api.core.recommendation.RecommendationService;
import com.siriusxi.ms.store.api.core.recommendation.dto.Recommendation;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * Class <code>RecommendationController</code> is the implementation of the main Recommendation
 * Endpoint API definition.
 *
 * @see RecommendationEndpoint
 * @author mohamed.taman
 * @version v4.0
 * @since v3.0 codename Storm
 */
@RestController
@Log4j2
public class RecommendationController implements RecommendationEndpoint {

  /** Recommendation service business logic interface. */
  private final RecommendationService recommendationService;

  @Autowired
  public RecommendationController(
      @Qualifier("RecommendationServiceImpl") RecommendationService recommendationService) {
    this.recommendationService = recommendationService;
  }

  /** {@inheritDoc} */
  @Override
  public Flux<Recommendation> getRecommendations(int productId) {
    return recommendationService.getRecommendations(productId);
  }
}
