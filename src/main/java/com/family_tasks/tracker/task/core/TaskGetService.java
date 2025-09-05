package com.family_tasks.tracker.task.core;

import com.family_tasks.tracker.task.infrastructure.TaskRepository;
import com.family_tasks.tracker.task.model.dto.TaskApiResponse;
import com.family_tasks.tracker.task.model.entity.TaskEntity;
import com.family_tasks.tracker.task.model.mupper.TaskGetMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.family_tasks.tracker.common.validation.ValidationMessage.TASK_NOT_EXIST;

@Service
@RequiredArgsConstructor
public class TaskGetService {

    private final TaskRepository taskRepository;
    private final TaskGetMapper mapper;
    private final TaskValidateService validateService;

    public TaskApiResponse getTask(String id, Integer userId) throws IllegalAccessException {

        Optional<TaskEntity> fromDB = taskRepository.findById(id);
        TaskEntity taskEntity = fromDB.orElseThrow(() -> new IllegalArgumentException(String.format(TASK_NOT_EXIST, id)));

        validateService.validateTaskGetting(userId, taskEntity);

        return mapper.toResponse(taskEntity);
    }
}