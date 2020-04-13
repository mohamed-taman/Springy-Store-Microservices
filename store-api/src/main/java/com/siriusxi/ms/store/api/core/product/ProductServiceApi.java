package com.siriusxi.ms.store.api.core.product;


import com.siriusxi.ms.store.api.core.product.dto.Product;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Interface <code>ProductServiceApi</code> is a higher level Interface to define <strong>Product
 * service</strong> endpoint APIs. and to be implemented by service controllers.
 *
 * @author mohamed.taman
 * @since v3.0 codename Storm
 * @version 1.0
 */
@RequestMapping("products")
public interface ProductServiceApi extends ProductService {

    /**
     * Sample usage:
     *
     *  <p>curl $HOST:$PORT/products/1</p>
     *
     * @param id is the product that you are looking for.
     * @return <code>Product</code> the product, if found, else null.
     */
    @Override
    @GetMapping(value = "{productId}",
            produces = APPLICATION_JSON_VALUE)
    Product getProduct(@PathVariable("productId") int id);

    /**
     * Sample usage:
     *
     * <p>curl -X POST $HOST:$PORT/products \ -H "Content-Type: application/json" --data \
     * '{"productId":123,"name":"product 123","weight":123}'</p>
     *
     * @param body product to save.
     * @return <code>Product</code> just created.
     */
    @Override
    @PostMapping(
            produces = APPLICATION_JSON_VALUE,
            consumes = APPLICATION_JSON_VALUE)
    Product createProduct(@RequestBody Product body);

    /**
     * Sample usage:
     *
     * <p>curl -X DELETE $HOST:$PORT/products/1</p>
     *
     * @param id to be deleted.
     */
    @Override
    @DeleteMapping("{productId}")
    void deleteProduct(@PathVariable("productId") int id);
}
