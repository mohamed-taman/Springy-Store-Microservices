package com.siriusxi.ms.store.api.composite;

import com.siriusxi.ms.store.api.composite.dto.ProductAggregate;

/**
 * Interface that define the general service contract (methods) for the Store
 *
 * <ol>
 *   <li>Service and,
 *   <li>Controller interfaces.
 * </ol>
 *
 * @author mohamed.taman
 * @version v0.2
 * @since v0.1
 */
public interface StoreService {

  /**
   * Add composite product to the product, review, and recommendation repositories.
   *
   * @see ProductAggregate
   * @param body product to save.
   * @since v0.1
   */
  void createProduct(ProductAggregate body);

  /**
   * Get the aggregate product with its reviews and recommendations and services involved in the
   * call.
   *
   * @see ProductAggregate
   * @param id is the product id that you are looking for.
   * @return the product, if found, else null.
   * @since v0.1
   */
  ProductAggregate getProduct(int id);

  /**
   * Delete the product and all its relate reviews and recommendations from their repositories.
   *
   * @see ProductAggregate
   * @implNote This method should be idempotent and always return 200 OK status.
   * @param id to be deleted.
   * @since v0.1
   */
  void deleteProduct(int id);
}
