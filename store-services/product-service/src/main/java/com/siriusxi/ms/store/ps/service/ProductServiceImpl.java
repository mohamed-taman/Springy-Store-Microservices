package com.siriusxi.ms.store.ps.service;

import com.siriusxi.ms.store.api.core.product.ProductService;
import com.siriusxi.ms.store.api.core.product.dto.Product;
import com.siriusxi.ms.store.ps.persistence.ProductRepository;
import com.siriusxi.ms.store.util.exceptions.InvalidInputException;
import com.siriusxi.ms.store.util.exceptions.NotFoundException;
import com.siriusxi.ms.store.util.http.ServiceUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static reactor.core.publisher.Mono.error;

@Service("ProductServiceImpl")
@Log4j2
public class ProductServiceImpl implements ProductService {

  private final ServiceUtil serviceUtil;

  private final ProductRepository repository;

  private final ProductMapper mapper;

  @Autowired
  public ProductServiceImpl(
      ProductRepository repository, ProductMapper mapper, ServiceUtil serviceUtil) {
    this.repository = repository;
    this.mapper = mapper;
    this.serviceUtil = serviceUtil;
  }

  @Override
  public Product createProduct(Product body) {

    isValidProductId(body.getProductId());

    return repository
            .save(mapper.apiToEntity(body))
            .log()
            .onErrorMap(
                    DuplicateKeyException.class,
                    ex -> new InvalidInputException("Duplicate key, Product Id: " + body.getProductId()))
            .map(mapper::entityToApi)
            .block();
  }

  @Override
  public Mono<Product> getProduct(int productId) {

    isValidProductId(productId);

    return repository
            .findByProductId(productId)
            .switchIfEmpty(error(new NotFoundException("No product found for productId: " + productId)))
            .log()
            .map(mapper::entityToApi)
            .map(e -> {
              e.setServiceAddress(serviceUtil.getServiceAddress());
              return e;
            });
  }

  /*
   Implementation is idempotent, that is,
   it will not report any failure if the entity is not found Always 200
  */
  @Override
  public void deleteProduct(int productId) {

    isValidProductId(productId);

    log.debug("deleteProduct: tries to delete an entity with productId: {}", productId);

    repository
            .findByProductId(productId)
            .log()
            .map(repository::delete)
            .flatMap(e -> e)
            .block();
  }

  // TODO Cloud be added to utilities class to be used by all core services implementations.
  private void isValidProductId(int productId) {
    if (productId < 1) throw new InvalidInputException("Invalid productId: " + productId);
  }
}
