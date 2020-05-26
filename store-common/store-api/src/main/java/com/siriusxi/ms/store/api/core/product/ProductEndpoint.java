package com.siriusxi.ms.store.api.core.product;

import com.siriusxi.ms.store.api.core.product.dto.Product;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Interface <code>ProductEndpoint</code> is a higher level Interface to define <strong>Product
 * Service</strong> endpoint APIs that follow <code>ProductService</code> interface. And to be
 * implemented by service controllers.
 *
 * @see ProductService
 * @author mohamed.taman
 * @version v5.8
 * @since v3.0 codename Storm
 */
@RequestMapping("products")
public interface ProductEndpoint extends ProductService {

  /**
   * Sample usage:
   *
   * <p><code>curl $HOST:$PORT/products/1</code>
   *
   * @param id is the product that you are looking for.
   * @return Product the product, if found, else null.
   * @since v3.0 codename Storm
   */
  @GetMapping(value = "{productId}", produces = APPLICATION_JSON_VALUE)
  @Override
  Mono<Product> getProduct(
      @PathVariable("productId") int id,
      @RequestParam(value = "delay", required = false, defaultValue = "0") int delay,
      @RequestParam(value = "faultPercent", required = false, defaultValue = "0") int faultPercent);
}
