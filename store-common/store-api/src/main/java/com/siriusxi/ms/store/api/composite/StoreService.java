package com.siriusxi.ms.store.api.composite;

import com.siriusxi.ms.store.api.composite.dto.ProductAggregate;
import reactor.core.publisher.Mono;

/**
 * Interface that define the general service contract (methods) for the Store
 *
 * <ol>
 *   <li>Service and,
 *   <li>Controller interfaces.
 * </ol>
 *
 * @author mohamed.taman
 * @version v5.8
 * @since v0.1
 */
public interface StoreService {

  /**
   * Add composite product to the product, review, and recommendation repositories.
   *
   * @see ProductAggregate
   * @param body product to save.
   * @since v0.1
   * @return void
   */
  Mono<Void> createProduct(ProductAggregate body);

  /**
   * Get the aggregate product with its reviews and recommendations and services involved in the
   * call.
   * It is a Non-Blocking API.
   *
   * @see ProductAggregate
   * @param id is the product id that you are looking for.
   * @param delay Causes the getProduct API on the product microservice to delay its response.
   * @param faultPercent Causes the getProduct API on the product microservice to throw an
   *                     exception randomly with the probability specified by the query parameter,
   *                     from 0 to 100%.
   * @return the product, if found, else null.
   * @since v0.1
   */
  Mono<ProductAggregate> getProduct(int id, int delay, int faultPercent);

  /**
   * Delete the product and all its relate reviews and recommendations from their repositories.
   *
   * @see ProductAggregate
   * @implNote This method should be idempotent and always return 200 OK status.
   * @param id to be deleted.
   * @since v0.1
   * @return void
   */
  Mono<Void> deleteProduct(int id);
}
