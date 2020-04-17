package com.siriusxi.ms.store.revs.persistence;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ReviewRepository extends CrudRepository<ReviewEntity, Integer> {

  @Transactional(readOnly = true)
  List<ReviewEntity> findByProductId(int productId);
}
