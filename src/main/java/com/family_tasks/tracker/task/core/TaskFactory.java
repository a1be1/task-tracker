package com.family_tasks.tracker.task.core;

import com.family_tasks.tracker.task.model.dto.CreateTaskApiRequest;
import com.family_tasks.tracker.task.model.entity.TaskEntity;
import com.family_tasks.tracker.task.model.enums.Priority;
import com.family_tasks.tracker.task.model.enums.TaskStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Component
public class TaskFactory {

    public TaskEntity createTask(CreateTaskApiRequest request) {
        log.info("Creating a new task. Request: {}", request);
        //TODO: consider generating ID by DB
        String id = UUID.randomUUID().toString();
        TaskEntity entity = TaskEntity.builder()
                .taskId(id)
                .status(TaskStatus.TO_DO)
                .name(request.getName())
                .description(request.getDescription())
                .priority(Priority.valueOf(request.getPriority()))
                .reporterId(request.getReporterId())
                .executorId(request.getExecutorId())
                .confidential(request.getConfidential())
                .sharedWith(request.getSharedWith())
                .deadline(request.getDeadline())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        log.info("Creating a new task. Result: {}", entity);
        return entity;
    }
}