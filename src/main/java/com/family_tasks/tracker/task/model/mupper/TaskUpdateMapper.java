package com.family_tasks.tracker.task.model.mupper;

import com.family_tasks.tracker.task.model.dto.TaskApiResponse;
import com.family_tasks.tracker.task.model.dto.UpdateTaskApiRequest;
import com.family_tasks.tracker.task.model.entity.TaskEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueMappingStrategy;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface TaskUpdateMapper {

    TaskApiResponse toResponse(TaskEntity entity);

    void fillWithTask(@MappingTarget TaskEntity.TaskEntityBuilder target, TaskEntity source);

    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "taskId", ignore = true)
    @Mapping(target = "reporterId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void fillWithRequest(@MappingTarget TaskEntity.TaskEntityBuilder target, UpdateTaskApiRequest source);
}