package com.siriusxi.ms.store.api.core.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class Product {
    private final int productId;
    private final String name;
    private final int weight;
    private final String serviceAddress;
}
