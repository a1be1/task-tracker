package com.family_tasks.tracker.task.application;

import com.family_tasks.tracker.task.core.CreateTaskService;
import com.family_tasks.tracker.task.core.UpdateTaskService;
import com.family_tasks.tracker.task.model.dto.CreateTaskApiRequest;
import com.family_tasks.tracker.task.model.dto.CreateTaskApiResponse;
import com.family_tasks.tracker.task.model.dto.UpdateTaskApiRequest;
import com.family_tasks.tracker.task.model.dto.UpdateTaskApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class TaskController {

    public static final String TASK_URL = "/v1/tasks";

    private final CreateTaskService createTaskService;
    private final UpdateTaskService updateTaskService;

    //TODO: implement validations
    @PostMapping(TASK_URL)
    public CreateTaskApiResponse createTask(@RequestBody CreateTaskApiRequest apiRequest) {
        return createTaskService.createTask(apiRequest);
    }

    //TODO: implement validations
    @PutMapping(TASK_URL + "/{taskId}")
    public UpdateTaskApiResponse updateTask(@PathVariable String taskId, @RequestBody UpdateTaskApiRequest apiRequest) {
        return updateTaskService.updateTask(taskId, apiRequest);
    }
}