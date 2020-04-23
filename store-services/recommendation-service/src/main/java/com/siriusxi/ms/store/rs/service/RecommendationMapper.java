package com.siriusxi.ms.store.rs.service;

import com.siriusxi.ms.store.api.core.recommendation.dto.Recommendation;
import com.siriusxi.ms.store.rs.persistence.RecommendationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RecommendationMapper {

  RecommendationMapper INSTANCE = Mappers.getMapper(RecommendationMapper.class);

  @Mapping(target = "rate", source = "entity.rating")
  @Mapping(target = "serviceAddress", ignore = true)
  Recommendation entityToApi(RecommendationEntity entity);

  @Mapping(target = "rating", source = "api.rate")
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "version", ignore = true)
  RecommendationEntity apiToEntity(Recommendation api);

  List<Recommendation> entityListToApiList(List<RecommendationEntity> entity);

  List<RecommendationEntity> apiListToEntityList(List<Recommendation> api);
}
