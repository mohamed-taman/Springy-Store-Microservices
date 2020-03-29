package com.siriusxi.ms.store.api.core.product;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor(force = true)
public class Product {
    private final int productId;
    private final String name;
    private final int weight;
    private final String serviceAddress;
}
