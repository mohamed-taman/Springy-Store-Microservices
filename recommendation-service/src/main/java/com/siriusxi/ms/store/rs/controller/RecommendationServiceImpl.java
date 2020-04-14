package com.siriusxi.ms.store.rs.controller;


import com.siriusxi.ms.store.api.core.recommendation.Recommendation;
import com.siriusxi.ms.store.api.core.recommendation.RecommendationService;
import com.siriusxi.ms.store.rs.persistence.RecommendationEntity;
import com.siriusxi.ms.store.rs.persistence.RecommendationRepository;
import com.siriusxi.ms.store.util.exceptions.InvalidInputException;
import com.siriusxi.ms.store.util.http.ServiceUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// FIXME to extract service from controller
@RestController
@Log4j2
public class RecommendationServiceImpl implements RecommendationService {

    private final RecommendationRepository repository;

    private final RecommendationMapper mapper;

    private final ServiceUtil serviceUtil;

    @Autowired
    public RecommendationServiceImpl(RecommendationRepository repository,
                                             RecommendationMapper mapper,
                                     ServiceUtil serviceUtil) {
        this.repository = repository;
        this.mapper = mapper;
        this.serviceUtil = serviceUtil;
    }

    @Override
    public Recommendation createRecommendation(Recommendation body) {
        try {
            RecommendationEntity entity = mapper.apiToEntity(body);
            RecommendationEntity newEntity = repository.save(entity);

            log.debug("createRecommendation: created a recommendation entity: {}/{}",
                    body.getProductId(), body.getRecommendationId());
            return mapper.entityToApi(newEntity);

        } catch (DuplicateKeyException dke) {
            throw new InvalidInputException("Duplicate key, Product Id: " + body.getProductId() + ", Recommendation Id:" + body.getRecommendationId());
        }
    }

    @Override
    public List<Recommendation> getRecommendations(int productId) {

        if (productId < 1) throw new InvalidInputException("Invalid productId: " + productId);

        List<RecommendationEntity> entityList = repository.findByProductId(productId);
        List<Recommendation> list = mapper.entityListToApiList(entityList);
        list.forEach(e -> e.setServiceAddress(serviceUtil.getServiceAddress()));

        log.debug("getRecommendations: response size: {}", list.size());

        return list;
    }

    @Override
    public void deleteRecommendations(int productId) {
        log.debug("deleteRecommendations: tries to delete recommendations for the product with " +
                "productId: {}", productId);
        repository.deleteAll(repository.findByProductId(productId));
    }
}
