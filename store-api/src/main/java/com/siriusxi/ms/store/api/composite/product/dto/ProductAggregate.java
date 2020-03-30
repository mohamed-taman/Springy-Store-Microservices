package com.siriusxi.ms.store.api.composite.product.dto;

import java.util.List;

//FIXME need a custom JSON serializer to be converted to JSON correctly
public record ProductAggregate(
        int productId,
        String name,
        int weight,
        List<RecommendationSummary> recommendations,
        List<ReviewSummary>reviews,
        ServiceAddresses serviceAddresses) {
}
