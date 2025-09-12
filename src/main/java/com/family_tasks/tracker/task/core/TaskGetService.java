package com.family_tasks.tracker.task.core;

import com.family_tasks.tracker.common.error.exception.NotFoundException;
import com.family_tasks.tracker.task.infrastructure.TaskRepository;
import com.family_tasks.tracker.task.model.dto.TaskApiResponse;
import com.family_tasks.tracker.task.model.entity.TaskEntity;
import com.family_tasks.tracker.task.model.mupper.TaskGetMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskGetService {

    private final TaskRepository taskRepository;
    private final TaskGetMapper mapper;
    private final TaskValidateService validateService;

    public TaskApiResponse getTask(String id, Integer userId) {

        Optional<TaskEntity> fromDB = taskRepository.findById(id);
        TaskEntity taskEntity = fromDB.orElseThrow(() -> NotFoundException.taskNotFound(id));

        validateService.validateTaskGetting(userId, taskEntity);

        return mapper.toResponse(taskEntity);
    }
}