package com.siriusxi.ms.store.api.composite.product.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class ServiceAddresses {
    private final String productCompositeService;
    private final String productService;
    private final String reviewService;
    private final String recommendationService;
}
