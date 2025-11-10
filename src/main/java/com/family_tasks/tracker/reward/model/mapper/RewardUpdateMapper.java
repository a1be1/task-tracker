package com.family_tasks.tracker.reward.model.mapper;

import com.family_tasks.tracker.reward.model.dto.RewardApiResponse;
import com.family_tasks.tracker.reward.model.dto.RewardUpdateApiRequest;
import com.family_tasks.tracker.reward.model.entity.RewardEntity;
import com.family_tasks.tracker.task.model.dto.TaskApiResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueMappingStrategy;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface RewardUpdateMapper {
    @Mapping(target = "rewardId", source = "id")
    RewardApiResponse toResponse(RewardEntity entity);

    @Mapping(target = "updatedAt", expression = "java(com.family_tasks.tracker.common.utils.TimeUtils.now())")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void fillWithRequest(@MappingTarget RewardEntity target, RewardUpdateApiRequest source);
}
