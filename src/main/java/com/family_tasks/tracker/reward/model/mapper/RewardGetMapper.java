package com.family_tasks.tracker.reward.model.mapper;

import com.family_tasks.tracker.reward.model.dto.RewardApiResponse;
import com.family_tasks.tracker.reward.model.entity.RewardEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface RewardGetMapper {
    @Mapping(target = "rewardId", source = "id")
    RewardApiResponse toResponse(RewardEntity entity);
}