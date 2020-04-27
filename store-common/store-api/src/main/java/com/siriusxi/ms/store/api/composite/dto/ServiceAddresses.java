package com.siriusxi.ms.store.api.composite.dto;

public record ServiceAddresses( String storeService,
                                String productService,
                                String reviewService,
                                String recommendationService) {
}
