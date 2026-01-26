package com.family_tasks.tracker.task.application;

import com.family_tasks.tracker.common.validation.annotation.ValidTaskFilter;
import com.family_tasks.tracker.task.core.TaskCreateService;
import com.family_tasks.tracker.task.core.TaskGetService;
import com.family_tasks.tracker.task.core.TaskUpdateService;
import com.family_tasks.tracker.task.model.dto.TaskApiResponse;
import com.family_tasks.tracker.task.model.dto.TaskCreateApiRequest;
import com.family_tasks.tracker.task.model.dto.TaskFilterRequest;
import com.family_tasks.tracker.task.model.dto.TaskUpdateApiRequest;
import com.family_tasks.tracker.task.model.enums.TaskFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.family_tasks.tracker.common.validation.ValidationMessage.TASK_FILTER_NOT_SPECIFIED;
import static com.family_tasks.tracker.common.validation.ValidationMessage.USER_NOT_SPECIFIED;

@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "Task management", description = "Operations for managing tasks")
public class TaskController {

    public static final String TASK_URL = "/v1/tasks";

    private final TaskCreateService taskCreateService;
    private final TaskUpdateService taskUpdateService;
    private final TaskGetService taskGetService;

    @Operation(
            summary = "Create a new task",
            description = """
                    This endpoint creates a new task in the system.
                    
                    **Rules and constraints:**
                    - Users without a group cannot create tasks.
                    - Executors and the task reporter must belong to the same group.
                    """
    )
    @PostMapping(TASK_URL)
    public TaskApiResponse createTask(@Valid @RequestBody TaskCreateApiRequest apiRequest) {
        return taskCreateService.createTask(apiRequest);
    }

    @Operation(
            summary = "Update an existing task",
            description = """
                    This endpoint updates an existing task in the system.
                    
                    **Rules and constraints:**
                    - Users without a group cannot update tasks.
                    - Executors and the task reporter must belong to the same group.
                    """
    )
    @PutMapping(TASK_URL + "/{taskId}")
    public TaskApiResponse updateTask(@PathVariable String taskId, @Valid @RequestBody TaskUpdateApiRequest apiRequest) {
        return taskUpdateService.updateTask(taskId, apiRequest);
    }

    @Operation(
            summary = "Get an existing task",
            description = """
                    This endpoint retrieves a task by its unique ID.
                    
                    **Rules and constraints:**
                    - If 'confidential' is 'true', only the task reporter or assigned executors can view the task.
                    """
    )
    @GetMapping(TASK_URL + "/{taskId}")
    public TaskApiResponse getTask(@PathVariable String taskId,
                                   @NotNull(message = USER_NOT_SPECIFIED)
                                   @RequestParam(name = "userId", required = false)
                                   Integer userId) {
        return taskGetService.getTask(taskId, userId);
    }

    @Operation(
            summary = "Get all tasks from group",
            description = """
                    This endpoint returns all tasks from a user's group, filtered by TaskFilter enum.
                    
                    **Filter values:**\n
                    'ALL_AVAILABLE': Returns all tasks visible to the current user, excluding confidential ones.\n
                    'ALL_CLOSED': Returns all tasks from the user's group that are marked as cancelled.\n
                    'IS_REPORTER_ACTIVE_TASK': Active (open) tasks **created by** the current user.\n
                    'IS_EXECUTOR_ACTIVE_TASK': Active (open) tasks **assigned to** the current user.\n
                    'IS_REPORTER_COMPLETED_TASK': Completed tasks **created by** the current user.\n
                    'IS_EXECUTOR_COMPLETED_TASK': Completed tasks **assigned to** the current user.
                    """
    )

    @GetMapping(TASK_URL)
    public Slice<TaskApiResponse> getTasks(@NotNull(message = USER_NOT_SPECIFIED)
                                           @RequestParam(name = "userId", required = false)
                                           Integer userId,
                                           @NotNull(message = TASK_FILTER_NOT_SPECIFIED)
                                           @ValidTaskFilter
                                           @RequestParam(name = "filter", required = false)
                                           String filter,
                                           Pageable page) {
        TaskFilterRequest taskFilterRequest = TaskFilterRequest.builder()
                .filter(TaskFilter.valueOf(filter))
                .userId(userId)
                .build();
        return taskGetService.getTasks(taskFilterRequest, page);
    }
}