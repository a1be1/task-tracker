package com.family_tasks.tracker.task.application;

import com.family_tasks.tracker.task.core.TaskCreateService;
import com.family_tasks.tracker.task.core.TaskUpdateService;
import com.family_tasks.tracker.task.model.dto.TaskApiResponse;
import com.family_tasks.tracker.task.model.dto.TaskCreateApiRequest;
import com.family_tasks.tracker.task.model.dto.TaskUpdateApiRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
public class TaskController {

    public static final String TASK_URL = "/v1/tasks";

    private final TaskCreateService taskCreateService;
    private final TaskUpdateService taskUpdateService;

    @PostMapping(TASK_URL)
    public TaskApiResponse createTask(@Valid @RequestBody TaskCreateApiRequest apiRequest) {
        return taskCreateService.createTask(apiRequest);
    }

    @PutMapping(TASK_URL + "/{taskId}")
    public TaskApiResponse updateTask(@PathVariable String taskId, @Valid @RequestBody TaskUpdateApiRequest apiRequest) {
        return taskUpdateService.updateTask(taskId, apiRequest);
    }
}