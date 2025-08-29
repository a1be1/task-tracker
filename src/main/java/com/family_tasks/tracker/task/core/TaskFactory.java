package com.family_tasks.tracker.task.core;

import com.family_tasks.tracker.task.model.dto.TaskCreateApiRequest;
import com.family_tasks.tracker.task.model.entity.TaskEntity;
import com.family_tasks.tracker.task.model.mupper.TaskCreateMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskFactory {

    private final TaskCreateMapper mapper;

    public TaskEntity createTask(TaskCreateApiRequest request) {
        log.info("Creating a new task. Request: {}", request);
        TaskEntity entity = mapper.toEntity(request);
        log.info("Creating a new task. Result: {}", entity);
        return entity;
    }
}