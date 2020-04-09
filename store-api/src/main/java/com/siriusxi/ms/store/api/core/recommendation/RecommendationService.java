package com.siriusxi.ms.store.api.core.recommendation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

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
            produces = APPLICATION_JSON_VALUE)
    List<Recommendation> getRecommendations(
            @RequestParam(value = "productId")
                    int productId);
}
