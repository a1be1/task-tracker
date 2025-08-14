package com.family_tasks.tracker.task.core;

import com.family_tasks.tracker.task.infrastructure.TaskRepository;
import com.family_tasks.tracker.task.model.dto.CreateTaskApiRequest;
import com.family_tasks.tracker.task.model.dto.TaskApiResponse;
import com.family_tasks.tracker.task.model.entity.TaskEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateTaskService {

    private final TaskFactory taskFactory;
    private final TaskRepository taskRepository;

    public TaskApiResponse createTask(CreateTaskApiRequest apiRequest) {
        //TODO: validate users existence
        TaskEntity taskEntity = taskFactory.createTask(apiRequest);
        taskRepository.saveTask(taskEntity);
        return toResponse(taskEntity);
    }

    private TaskApiResponse toResponse(TaskEntity taskEntity) {
        return TaskApiResponse.builder()
                .taskId(taskEntity.getTaskId())
                .status(taskEntity.getStatus())
                .name(taskEntity.getName())
                .description(taskEntity.getDescription())
                .priority(taskEntity.getPriority())
                .reporterId(taskEntity.getReporterId())
                .executorId(taskEntity.getExecutorId())
                .confidential(taskEntity.isConfidential())
                .sharedWith(taskEntity.getSharedWith())
                .deadline(taskEntity.getDeadline())
                .createdAt(taskEntity.getCreatedAt())
                .updatedAt(taskEntity.getUpdatedAt())
                .build();
    }
}