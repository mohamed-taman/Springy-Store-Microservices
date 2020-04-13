package com.siriusxi.ms.store.ps.api;

import com.siriusxi.ms.store.api.core.product.ProductService;
import com.siriusxi.ms.store.api.core.product.ProductServiceApi;
import com.siriusxi.ms.store.api.core.product.dto.Product;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RestController;

/**
 * Class <code>ProductController</code> is the implementation of the main product API definition.
 * @see com.siriusxi.ms.store.api.core.product.ProductServiceApi
 *
 * @author mohamed.taman
 * @since v3.0 codename Storm
 * @version 1.0
 */
@RestController
@Log4j2
public class ProductController implements ProductServiceApi {

    /**
     * Product service business logic interface.
     */
    private final ProductService prodService;

    @Autowired
    public ProductController(@Qualifier("ProductServiceImpl") ProductService prodService) {
        this.prodService = prodService;
    }

  /**
   * {@inheritDoc}
   */
  @Override
  public Product getProduct(int id) {
        return prodService.getProduct(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Product createProduct(Product body) {
        return prodService.createProduct(body);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteProduct(int id) {
        prodService.deleteProduct(id);
    }
}
