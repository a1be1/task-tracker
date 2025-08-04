package com.family_tasks.tracker.task.core;

import com.family_tasks.tracker.task.model.dto.CreateTaskApiRequest;
import com.family_tasks.tracker.task.model.entity.TaskEntity;
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
                .name(request.getName())
                .description(request.getDescription())
                .priority(request.getPriority())
                .reporter(request.getReporter())
                .executor(request.getExecutor())
                .isPrivate(request.getIsPrivate())
                .sharedWith(request.getSharedWith())
                .deadline(request.getDeadline())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        log.info("Creating a new user. Result: {}", entity);
        return entity;
    }
}