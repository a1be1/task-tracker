package com.family_tasks.tracker.user.model.mapper;

import com.family_tasks.tracker.user.model.dto.CreateUserApiRequest;
import com.family_tasks.tracker.user.model.dto.CreateUserApiResponse;
import com.family_tasks.tracker.user.model.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueMappingStrategy;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface UserMapper {

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    void fillWithRequest(@MappingTarget UserEntity.UserEntityBuilder builder,
                         CreateUserApiRequest request);

    @Mapping(target = "userId", source = "id")
    CreateUserApiResponse toResponse(UserEntity entity);
}
