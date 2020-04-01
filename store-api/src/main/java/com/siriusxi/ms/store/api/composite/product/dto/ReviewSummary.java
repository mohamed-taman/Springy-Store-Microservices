package com.siriusxi.ms.store.api.composite.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class ReviewSummary {
    private final int reviewId;
    private final String author;
    private final String subject;
}
