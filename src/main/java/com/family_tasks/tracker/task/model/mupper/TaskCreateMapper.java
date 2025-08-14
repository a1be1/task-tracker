package com.family_tasks.tracker.task.model.mupper;

import com.family_tasks.tracker.task.model.dto.CreateTaskApiRequest;
import com.family_tasks.tracker.task.model.dto.TaskApiResponse;
import com.family_tasks.tracker.task.model.entity.TaskEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface TaskCreateMapper {

    TaskApiResponse toResponse(TaskEntity entity);

    @Mapping(target = "taskId", expression = "java(java.util.UUID.randomUUID().toString())")
    @Mapping(target = "status", constant = "TO_DO")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    TaskEntity toEntity(CreateTaskApiRequest request);
}