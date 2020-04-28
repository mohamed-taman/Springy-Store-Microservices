package com.siriusxi.ms.store.api.composite.dto;

import java.util.List;

public record ProductAggregate (int productId,
                                String name,
                                int weight,
                                List<RecommendationSummary> recommendations,
                                List<ReviewSummary>reviews,
                                ServiceAddresses serviceAddresses) {
}