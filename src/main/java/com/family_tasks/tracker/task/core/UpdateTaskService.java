package com.family_tasks.tracker.task.core;

import com.family_tasks.tracker.task.infrastructure.TaskRepository;
import com.family_tasks.tracker.task.model.dto.TaskApiResponse;
import com.family_tasks.tracker.task.model.dto.UpdateTaskApiRequest;
import com.family_tasks.tracker.task.model.entity.TaskEntity;
import com.family_tasks.tracker.task.model.enums.Priority;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UpdateTaskService {
    private final TaskRepository taskRepository;

    public TaskApiResponse updateTask(String taskId, UpdateTaskApiRequest apiRequest) {
        //TODO: validate users existence
        TaskEntity taskEntity = taskRepository.getTask(taskId);
        taskEntity.setName(apiRequest.getName());
        taskEntity.setDescription(apiRequest.getDescription());
        taskEntity.setPriority(Priority.valueOf(apiRequest.getPriority()));
        taskEntity.setExecutorId(apiRequest.getExecutorId());
        taskEntity.setConfidential(apiRequest.getConfidential());
        taskEntity.setSharedWith(apiRequest.getSharedWith());
        taskEntity.setDeadline(apiRequest.getDeadline());
        taskEntity.setUpdatedAt(LocalDateTime.now());

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