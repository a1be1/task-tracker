package com.family_tasks.tracker.task.core;

import com.family_tasks.tracker.task.infrastructure.TaskRepository;
import com.family_tasks.tracker.task.model.dto.CreateTaskApiRequest;
import com.family_tasks.tracker.task.model.dto.TaskApiResponse;
import com.family_tasks.tracker.task.model.entity.TaskEntity;
import com.family_tasks.tracker.task.model.mupper.TaskCreateMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateTaskService {

    private final TaskFactory taskFactory;
    private final TaskRepository taskRepository;
    private final TaskCreateMapper mapper;

    public TaskApiResponse createTask(CreateTaskApiRequest apiRequest) {
        //TODO: validate users existence
        TaskEntity taskEntity = taskFactory.createTask(apiRequest);
        taskRepository.saveTask(taskEntity);
        return mapper.toResponse(taskEntity);
    }
}