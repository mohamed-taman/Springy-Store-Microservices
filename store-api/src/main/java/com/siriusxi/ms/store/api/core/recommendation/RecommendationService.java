package com.siriusxi.ms.store.api.core.recommendation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface RecommendationService {

    /**
     * Sample usage: curl $HOST:$PORT/recommendation?productId=1
     *
     * @param productId that you are looking for its recommendations.
     * @return list of recommendations for this product,
     * or empty list if there are no recommendations.
     */
    @GetMapping(
            value = "/recommendation",
            produces = "application/json")
    List<Recommendation> getRecommendations(
            @RequestParam(value = "productId")
                    int productId);
}
