package com.siriusxi.ms.store.api.composite.product.dto;

public record RecommendationSummary(
        int recommendationId,
        String author,
        int rate) {
}
