package com.siriusxi.ms.store.api.core.review;
//FIXME need a custom JSON serializer to be converted to JSON correctly
public record Review(
        int productId,
        int reviewId,
        String author,
        String subject,
        String content,
        String serviceAddress) {
}
