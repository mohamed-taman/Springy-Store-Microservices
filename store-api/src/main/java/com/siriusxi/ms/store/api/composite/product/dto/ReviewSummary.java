package com.siriusxi.ms.store.api.composite.product.dto;

//FIXME need a custom JSON serializer to be converted to JSON correctly
public record ReviewSummary(
        int reviewId,
        String author,
        String subject) {
}
