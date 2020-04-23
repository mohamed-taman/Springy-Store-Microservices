package com.siriusxi.ms.store.rs.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

//FIXME to be documented and to be pageable

@Repository
public interface RecommendationRepository
    extends ReactiveCrudRepository<RecommendationEntity, String> {

  Flux<RecommendationEntity> findByProductId(int productId);
}
