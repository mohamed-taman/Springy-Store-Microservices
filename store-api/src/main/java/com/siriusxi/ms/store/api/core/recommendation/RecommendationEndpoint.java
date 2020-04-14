package com.siriusxi.ms.store.api.core.recommendation;

import com.siriusxi.ms.store.api.core.recommendation.dto.Recommendation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
/**
 * Interface <code>RecommendationEndpoint</code> is a higher level Interface to define
 * <strong>Recommendation Service</strong> endpoint APIs, that follow <code>RecommendationService
 * </code> interface. And to be implemented by service controllers.
 *
 * @see com.siriusxi.ms.store.api.core.recommendation.RecommendationService
 * @author mohamed.taman
 * @version v1.0
 * @since v3.0 codename Storm
 */
@RequestMapping("recommendations")
public interface RecommendationEndpoint extends RecommendationService {

  /**
   * Sample usage:
   *
   *  <p><code>curl $HOST:$PORT/recommendations?productId=1</code></p>
   *
   * @param productId that you are looking for its recommendations.
   * @return list of product recommendations, or empty list if there are no recommendations.
   * @since v3.0 codename Storm
   */
  @GetMapping(produces = APPLICATION_JSON_VALUE)
  List<Recommendation> getRecommendations(@RequestParam("productId") int productId);

  /**
   * Sample usage:
   *
   * <p><code>curl -X POST $HOST:$PORT/recommendations \
   * -H "Content-Type: application/json" --data \
   * '{"productId":123,"recommendationId":456,"author":"me","rate":5,"content":"yada, yada, yada"
   * }'</code></p>
   *
   * @param body the recommendation to add.
   * @return currently created recommendation.
   * @since v3.0 codename Storm
   */
  @PostMapping(produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
  Recommendation createRecommendation(@RequestBody Recommendation body);

  /**
   * Sample usage:
   *
   * <p><code>curl -X DELETE $HOST:$PORT/recommendations?productId=1</code></p>
   *
   * @param productId to delete recommendations for.
   * @since version 0.1
   */
  @DeleteMapping
  void deleteRecommendations(@RequestParam("productId") int productId);
}
