package com.siriusxi.ms.store.api.composite.dto;

public record RecommendationSummary(int recommendationId,
                                    String author,
                                    int rate,
                                    String content) {
}