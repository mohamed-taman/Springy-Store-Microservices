package com.siriusxi.ms.store.api.composite.product;

import com.siriusxi.ms.store.api.composite.product.dto.ProductAggregate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface ProductCompositeService {

    /**
     * Sample usage: curl $HOST:$PORT/product-composite/1
     *
     * @param productId is the product that you are looking for.
     * @return the composite product info, if found, else null.
     */
    @GetMapping(
            value = "/product-composite/{productId}",
            produces = "application/json")
    ProductAggregate getProduct(@PathVariable int productId);
}
