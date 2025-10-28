package com.family_tasks.tracker.user.model.mapper;

import com.family_tasks.tracker.user.model.dto.UserApiResponse;
import com.family_tasks.tracker.user.model.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface UserGetMapper {
    @Mapping(target="userId", source = "id")
    UserApiResponse toResponse(UserEntity entity);
}
