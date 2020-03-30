package com.siriusxi.ms.store.api.composite.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class RecommendationSummary {
    private final int recommendationId;
    private final String author;
    private final int rate;
}
