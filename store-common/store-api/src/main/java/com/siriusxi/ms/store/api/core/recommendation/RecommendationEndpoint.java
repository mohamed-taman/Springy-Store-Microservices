package com.siriusxi.ms.store.api.core.recommendation;

import com.siriusxi.ms.store.api.core.recommendation.dto.Recommendation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
/**
 * Interface <code>RecommendationEndpoint</code> is a higher level Interface to define
 * <strong>Recommendation Service</strong> endpoint APIs, that follow <code>RecommendationService
 * </code> interface. And to be implemented by service controllers.
 *
 * @see RecommendationService
 * @author mohamed.taman
 * @version v4.0
 * @since v3.0 codename Storm
 */
@RequestMapping("recommendations")
public interface RecommendationEndpoint extends RecommendationService {

  /**
   * Sample usage:
   *
   * <p><code>curl $HOST:$PORT/recommendations?productId=1</code>
   *
   * @param productId that you are looking for its recommendations.
   * @return list of product recommendations, or empty list if there are no recommendations.
   * @since v3.0 codename Storm
   */
  @GetMapping(produces = APPLICATION_JSON_VALUE)
  @Override
  Flux<Recommendation> getRecommendations(@RequestParam("productId") int productId);
}
