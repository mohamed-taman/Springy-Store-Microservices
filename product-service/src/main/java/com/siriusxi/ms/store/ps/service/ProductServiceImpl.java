package com.siriusxi.ms.store.ps.service;

import com.siriusxi.ms.store.api.core.product.ProductService;
import com.siriusxi.ms.store.api.core.product.dto.Product;
import com.siriusxi.ms.store.ps.persistence.ProductEntity;
import com.siriusxi.ms.store.ps.persistence.ProductRepository;
import com.siriusxi.ms.store.util.exceptions.InvalidInputException;
import com.siriusxi.ms.store.util.exceptions.NotFoundException;
import com.siriusxi.ms.store.util.http.ServiceUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

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
    try {
      ProductEntity entity = mapper.apiToEntity(body);
      ProductEntity newEntity = repository.save(entity);

      log.debug("createProduct: entity created for productId: {}", body.getProductId());
      return mapper.entityToApi(newEntity);

    } catch (DuplicateKeyException dke) {
      throw new InvalidInputException("Duplicate key, Product Id: " + body.getProductId());
    }
  }

  @Override
  public Product getProduct(int productId) {
    if (productId < 1) throw new InvalidInputException("Invalid productId: " + productId);

    ProductEntity entity =
        repository
            .findByProductId(productId)
            .orElseThrow(
                () -> new NotFoundException("No product found for productId: " + productId));

    Product response = mapper.entityToApi(entity);
    response.setServiceAddress(serviceUtil.getServiceAddress());

    log.debug("getProduct: found productId: {}", response.getProductId());

    return response;
  }

  /*
   Implementation is idempotent, that is,
   it will not report any failure if the entity is not found Always 200
  */
  @Override
  public void deleteProduct(int productId) {
    log.debug("deleteProduct: tries to delete an entity with productId: {}", productId);
    repository.findByProductId(productId).ifPresent(repository::delete);
  }
}
