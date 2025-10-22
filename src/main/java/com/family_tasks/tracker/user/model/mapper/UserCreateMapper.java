package com.family_tasks.tracker.user.model.mapper;

import com.family_tasks.tracker.user.model.dto.CreateUserApiRequest;
import com.family_tasks.tracker.user.model.dto.UserApiResponse;
import com.family_tasks.tracker.user.model.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface UserCreateMapper {

    @Mapping(target = "userId", source = "id")
    UserApiResponse toResponse(UserEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", expression = "java(com.family_tasks.tracker.common.utils.TimeUtils.now())")
    @Mapping(target = "updatedAt", expression = "java(com.family_tasks.tracker.common.utils.TimeUtils.now())")
    UserEntity toEntity(CreateUserApiRequest request);
}
