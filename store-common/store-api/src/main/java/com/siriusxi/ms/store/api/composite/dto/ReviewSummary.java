package com.siriusxi.ms.store.api.composite.dto;

public record ReviewSummary(int reviewId,
                            String author,
                            String subject,
                            String content){
}
