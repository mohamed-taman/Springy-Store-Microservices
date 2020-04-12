package com.siriusxi.ms.store.api.core.recommendation;

import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

//@RequestMapping("recommendations")
public interface RecommendationService {

  /**
   * Sample usage: curl $HOST:$PORT/recommendations?productId=1
   *
   * @param productId that you are looking for its recommendations.
   *
   * @return list of product recommendations,
   * or empty list if there are no recommendations.
   */
  @GetMapping(value = "recommendations",produces = APPLICATION_JSON_VALUE)
  List<Recommendation> getRecommendations(@RequestParam("productId") int productId);

  /**
   * Sample usage:
   *
   * <p>curl -X POST $HOST:$PORT/recommendations \ -H "Content-Type: application/json" --data \
   * '{"productId":123,"recommendationId":456,"author":"me","rate":5,"content":"yada, yada, yada"}'
   *
   * @param body the recommendation to add.
   * @return currently created recommendation.
   */
  @PostMapping(value = "recommendations",
          produces = APPLICATION_JSON_VALUE,
          consumes = APPLICATION_JSON_VALUE)
  Recommendation createRecommendation(@RequestBody Recommendation body);

  /**
   * Sample usage:
   *
   * <p>curl -X DELETE $HOST:$PORT/recommendations?productId=1
   *
   * @param productId to delete recommendations for.
   */
  @DeleteMapping(value = "recommendations")
  void deleteRecommendations(@RequestParam("productId") int productId);
}
