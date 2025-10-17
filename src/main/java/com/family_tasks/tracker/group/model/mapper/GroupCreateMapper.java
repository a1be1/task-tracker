package com.family_tasks.tracker.group.model.mapper;

import com.family_tasks.tracker.group.model.dto.GroupApiResponse;
import com.family_tasks.tracker.group.model.dto.GroupCreateApiRequest;
import com.family_tasks.tracker.group.model.entity.GroupEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface GroupCreateMapper {
    @Mapping(target = "groupId", source = "id")
    GroupApiResponse toResponse(GroupEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", expression = "java(com.family_tasks.tracker.common.utils.TimeUtils.now())")
    @Mapping(target = "updatedAt", expression = "java(com.family_tasks.tracker.common.utils.TimeUtils.now())")
    @Mapping(target = "deletedAt", ignore = true)
    GroupEntity toEntity(GroupCreateApiRequest request);
}