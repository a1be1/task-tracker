package com.family_tasks.tracker.task.model.mupper;

import com.family_tasks.tracker.task.model.dto.TaskApiResponse;
import com.family_tasks.tracker.task.model.dto.TaskUpdateApiRequest;
import com.family_tasks.tracker.task.model.entity.TaskEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueMappingStrategy;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface TaskUpdateMapper {
    @Mapping(target = "taskId", source = "id")
    TaskApiResponse toResponse(TaskEntity entity);

    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "reporterId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void fillWithRequest(@MappingTarget TaskEntity target, TaskUpdateApiRequest source);
}