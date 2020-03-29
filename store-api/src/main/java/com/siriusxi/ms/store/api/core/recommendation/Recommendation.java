package com.siriusxi.ms.store.api.core.recommendation;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(force = true)
public class Recommendation {
    private final int productId;
    private final int recommendationId;
    private final String author;
    private final int rate;
    private final String content;
    private final String serviceAddress;
}
