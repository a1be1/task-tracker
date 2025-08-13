package com.family_tasks.tracker.task.application;

import com.family_tasks.tracker.task.core.CreateTaskService;
import com.family_tasks.tracker.task.core.UpdateTaskService;
import com.family_tasks.tracker.task.model.dto.CreateTaskApiRequest;
import com.family_tasks.tracker.task.model.dto.CreateTaskApiResponse;
import com.family_tasks.tracker.task.model.dto.UpdateTaskApiRequest;
import com.family_tasks.tracker.task.model.dto.UpdateTaskApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequiredArgsConstructor
public class TaskController {

    public static final String TASK_URL = "/v1/tasks";

    private final CreateTaskService createTaskService;
    private final UpdateTaskService updateTaskService;

    @PostMapping(TASK_URL)
    public CreateTaskApiResponse createTask(@Valid @RequestBody CreateTaskApiRequest apiRequest) {
        return createTaskService.createTask(apiRequest);
    }

    @PutMapping(TASK_URL + "/{taskId}")
    public UpdateTaskApiResponse updateTask(@PathVariable String taskId, @Valid @RequestBody UpdateTaskApiRequest apiRequest) {
        return updateTaskService.updateTask(taskId, apiRequest);
    }
}