package com.siriusxi.ms.store.ps.api;

import com.siriusxi.ms.store.api.core.product.ProductEndpoint;
import com.siriusxi.ms.store.api.core.product.ProductService;
import com.siriusxi.ms.store.api.core.product.dto.Product;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * Class <code>ProductController</code> is the implementation of the main Product Endpoint API
 * definition.
 *
 * @see ProductEndpoint
 * @author mohamed.taman
 * @version v5.8
 * @since v3.0 codename Storm
 */
@RestController
@Log4j2
public class ProductController implements ProductEndpoint {

  /** Product service business logic interface. */
  private final ProductService prodService;

  @Autowired
  public ProductController(@Qualifier("ProductServiceImpl") ProductService prodService) {
    this.prodService = prodService;
  }

  /** {@inheritDoc} */
  @Override
  public Mono<Product> getProduct(int id, int delay, int faultPercent) {
    return prodService.getProduct(id, delay, faultPercent);
  }
}
