package com.siriusxi.ms.store.api.core.review;

public record Review(
        int productId,
        int reviewId,
        String author,
        String subject,
        String content,
        String serviceAddress) {
}
