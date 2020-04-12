package com.siriusxi.ms.store.api.composite.dto;

import lombok.*;

import java.util.List;

// TODO convert it to record
@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class ProductAggregate {
    private final int productId;
    private final String name;
    private final int weight;
    private final List<RecommendationSummary> recommendations;
    private final List<ReviewSummary> reviews;
    private final ServiceAddresses serviceAddresses;
}
