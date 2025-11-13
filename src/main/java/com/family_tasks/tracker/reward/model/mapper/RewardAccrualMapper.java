package com.family_tasks.tracker.reward.model.mapper;

import com.family_tasks.tracker.reward.model.dto.RewardAccrualRequest;
import com.family_tasks.tracker.reward.model.entity.RewardEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface RewardAccrualMapper {

    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID().toString())")
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "totalSum", ignore = true)
    @Mapping(target = "createdAt", expression = "java(com.family_tasks.tracker.common.utils.TimeUtils.now())")
    @Mapping(target = "updatedAt", expression = "java(com.family_tasks.tracker.common.utils.TimeUtils.now())")
    RewardEntity toEntity(RewardAccrualRequest request);
}