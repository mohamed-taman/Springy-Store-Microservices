package com.siriusxi.ms.store.ps.persistence;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends PagingAndSortingRepository<ProductEntity, String> {

  Optional<ProductEntity> findByProductId(int productId);
}
