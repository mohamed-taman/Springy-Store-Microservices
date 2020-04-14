package com.siriusxi.ms.store.api.core.product;

import com.siriusxi.ms.store.api.core.product.dto.Product;

/**
 * Interface that define the general service contract (methods) for the Product
 * <ol>
 *     <li>Service and,</li>
 *     <li>Controller interfaces.</li>
 * </ol>
 *
 * @author mohamed.taman
 * @version v0.2
 * @since v0.1
 */
public interface ProductService {

  /**
   * Get the product with Id from repository.
   *
   * @param id is the product id that you are looking for.
   * @return the product, if found, else null.
   * @since v0.1
   */
  Product getProduct(int id);

  /**
   * Add product to the repository.
   *
   * @param body product to save.
   * @return just created product.
   * @since v0.1
   */
  Product createProduct(Product body);

  /**
   * Delete the product from repository.
   *
   * @implNote This method should be idempotent and always return 200 OK status.
   * @param id to be deleted.
   * @since v0.1
   */
  void deleteProduct(int id);
}
