package com.family_tasks.tracker.task.application;

import com.family_tasks.tracker.task.core.CreateTaskService;
import com.family_tasks.tracker.task.model.dto.CreateTaskApiRequest;
import com.family_tasks.tracker.task.model.dto.CreateTaskApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TaskController {

    public static final String TASK_URL = "/v1/tasks";

    private final CreateTaskService createTaskService;

    //TODO: implement validations
    @PostMapping(TASK_URL)
    public CreateTaskApiResponse createTask(@RequestBody CreateTaskApiRequest apiRequest) {
        return createTaskService.createTask(apiRequest);
    }
}
