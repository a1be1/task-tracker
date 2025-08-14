package com.family_tasks.tracker.task.core;

import com.family_tasks.tracker.task.infrastructure.TaskRepository;
import com.family_tasks.tracker.task.model.dto.TaskApiResponse;
import com.family_tasks.tracker.task.model.dto.UpdateTaskApiRequest;
import com.family_tasks.tracker.task.model.entity.TaskEntity;
import com.family_tasks.tracker.task.model.mupper.TaskUpdateMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateTaskService {
    private final TaskRepository taskRepository;
    private final TaskUpdateMapper mapper;

    public TaskApiResponse updateTask(String taskId, UpdateTaskApiRequest apiRequest) {
        //TODO: validate users existence
        TaskEntity fromDB = taskRepository.getTask(taskId);
        TaskEntity.TaskEntityBuilder updatedTaskBuilder = TaskEntity.builder();
        mapper.fillWithTask(updatedTaskBuilder, fromDB);
        mapper.fillWithRequest(updatedTaskBuilder, apiRequest);

        TaskEntity updatedTask = updatedTaskBuilder.build();
        taskRepository.saveTask(updatedTask);
        return mapper.toResponse(updatedTask);
    }
}