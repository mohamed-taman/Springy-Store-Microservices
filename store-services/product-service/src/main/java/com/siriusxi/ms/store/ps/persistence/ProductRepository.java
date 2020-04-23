package com.siriusxi.ms.store.ps.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ProductRepository extends ReactiveCrudRepository<ProductEntity, String> {

  Mono<ProductEntity> findByProductId(int productId);
}
