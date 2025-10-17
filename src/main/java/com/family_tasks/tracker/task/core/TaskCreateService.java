package com.family_tasks.tracker.task.core;

import com.family_tasks.tracker.task.infrastructure.TaskRepository;
import com.family_tasks.tracker.task.model.dto.TaskApiResponse;
import com.family_tasks.tracker.task.model.dto.TaskCreateApiRequest;
import com.family_tasks.tracker.task.model.entity.TaskEntity;
import com.family_tasks.tracker.task.model.mapper.TaskCreateMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskCreateService {

    private final TaskFactory taskFactory;
    private final TaskRepository taskRepository;
    private final TaskCreateMapper mapper;
    private final TaskValidateService validateService;

    public TaskApiResponse createTask(TaskCreateApiRequest apiRequest) {

        validateService.validateTaskCreation(apiRequest);
        TaskEntity taskEntity = taskFactory.createTask(apiRequest);
        taskRepository.save(taskEntity);
        return mapper.toResponse(taskEntity);
    }
}