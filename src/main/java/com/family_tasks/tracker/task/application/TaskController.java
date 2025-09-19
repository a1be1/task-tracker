package com.family_tasks.tracker.task.application;

import com.family_tasks.tracker.common.validation.annotation.ValidTaskFilter;
import com.family_tasks.tracker.task.core.TaskCreateService;
import com.family_tasks.tracker.task.core.TaskGetService;
import com.family_tasks.tracker.task.core.TaskUpdateService;
import com.family_tasks.tracker.task.model.dto.TaskApiResponse;
import com.family_tasks.tracker.task.model.dto.TaskCreateApiRequest;
import com.family_tasks.tracker.task.model.dto.TaskFilterRequest;
import com.family_tasks.tracker.task.model.dto.TaskUpdateApiRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.family_tasks.tracker.common.validation.ValidationMessage.FILTER_NOT_SPECIFIED;
import static com.family_tasks.tracker.common.validation.ValidationMessage.USER_NOT_SPECIFIED;

@Validated
@RestController
@RequiredArgsConstructor
public class TaskController {

    public static final String TASK_URL = "/v1/tasks";

    private final TaskCreateService taskCreateService;
    private final TaskUpdateService taskUpdateService;
    private final TaskGetService taskGetService;

    @PostMapping(TASK_URL)
    public TaskApiResponse createTask(@Valid @RequestBody TaskCreateApiRequest apiRequest) {
        return taskCreateService.createTask(apiRequest);
    }

    @PutMapping(TASK_URL + "/{taskId}")
    public TaskApiResponse updateTask(@PathVariable String taskId, @Valid @RequestBody TaskUpdateApiRequest apiRequest) {
        return taskUpdateService.updateTask(taskId, apiRequest);
    }

    @GetMapping(TASK_URL + "/{taskId}")
    public TaskApiResponse getTask(@PathVariable String taskId,
                                   @NotNull(message = USER_NOT_SPECIFIED)
                                   @RequestParam(name = "userId", required = false)
                                   Integer userId) {
        return taskGetService.getTask(taskId, userId);
    }

    @GetMapping(TASK_URL + "/all")
    public List<TaskApiResponse> getTasks(@NotNull(message = USER_NOT_SPECIFIED)
                                          @RequestParam(name = "userId", required = false)
                                          Integer userId,
                                          @NotNull(message = FILTER_NOT_SPECIFIED)
                                          @ValidTaskFilter
                                          @RequestParam(name = "filter", required = false)
                                          String filter) {
        TaskFilterRequest taskFilterRequest = TaskFilterRequest.builder()
                .filter(filter)
                .userId(userId)
                .build();
        return taskGetService.getTasks(taskFilterRequest);
    }
}