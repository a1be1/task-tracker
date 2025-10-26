package com.family_tasks.tracker.user.model.mapper;

import com.family_tasks.tracker.user.model.dto.UserApiResponse;
import com.family_tasks.tracker.user.model.dto.UserUpdateApiRequest;
import com.family_tasks.tracker.user.model.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueMappingStrategy;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface UserUpdateMapper {
    @Mapping(target = "userId", source = "id")
    UserApiResponse toResponse(UserEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(com.family_tasks.tracker.common.utils.TimeUtils.now())")
    void fillWithRequest(@MappingTarget UserEntity user, UserUpdateApiRequest source);
}