package com.siriusxi.ms.store.api.core.product;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface ProductService {
    /**
     * Sample usage: curl $HOST:$PORT/product/1
     *
     * @param productId is the product that you are looking for.
     * @return the product, if found, else null.
     */
    @GetMapping(
            value = "/product/{productId}",
            produces = "application/json")
    Product getProduct(@PathVariable int productId);
}
