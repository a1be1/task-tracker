package com.family_tasks.tracker.task.model.mupper;

import com.family_tasks.tracker.task.model.dto.TaskApiResponse;
import com.family_tasks.tracker.task.model.entity.TaskEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;

/*when it meets empty collection it returns empty collection instead of NULL*/
@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface TaskGetMapper {
    @Mapping(target = "taskId", source = "id")
    TaskApiResponse toResponse(TaskEntity entity);
}