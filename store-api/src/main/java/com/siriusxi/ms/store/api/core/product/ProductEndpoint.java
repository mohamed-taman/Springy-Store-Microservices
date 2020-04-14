package com.siriusxi.ms.store.api.core.product;


import com.siriusxi.ms.store.api.core.product.dto.Product;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Interface <code>ProductEndpoint</code> is a higher level Interface
 * to define <strong>Product Service</strong> endpoint APIs that follow <code>ProductService</code>
 * interface. And to be implemented by service controllers.
 *
 * @see ProductService
 *
 * @author mohamed.taman
 * @version v1.0
 * @since v3.0 codename Storm
 */
@RequestMapping("products")
public interface ProductEndpoint extends ProductService {

    /**
     * Sample usage:
     *
     *  <p><code>curl $HOST:$PORT/products/1</code></p>
     *
     * @param id is the product that you are looking for.
     * @return Product the product, if found, else null.
     * @since v3.0 codename Storm
     */
    @Override
    @GetMapping(value = "{productId}",
            produces = APPLICATION_JSON_VALUE)
    Product getProduct(@PathVariable("productId") int id);

    /**
     * Sample usage:
     *
     * <p><code>curl -X POST $HOST:$PORT/products \ -H "Content-Type: application/json" --data \
     * '{"productId":123,"name":"product 123","weight":123}'</code></p>
     *
     * @param body product to save.
     * @return Product just created.
     * @since v3.0 codename Storm
     */
    @Override
    @PostMapping(
            produces = APPLICATION_JSON_VALUE,
            consumes = APPLICATION_JSON_VALUE)
    Product createProduct(@RequestBody Product body);

    /**
     * Sample usage:
     *
     * <p><code>curl -X DELETE $HOST:$PORT/products/1</code></p>
     *
     * @param id to be deleted.
     * @since v3.0 codename Storm
     */
    @Override
    @DeleteMapping("{productId}")
    void deleteProduct(@PathVariable("productId") int id);
}
