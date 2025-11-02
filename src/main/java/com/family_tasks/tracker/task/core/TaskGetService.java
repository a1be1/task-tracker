package com.family_tasks.tracker.task.core;

import com.family_tasks.tracker.common.error.exception.NotFoundException;
import com.family_tasks.tracker.task.infrastructure.TaskRepository;
import com.family_tasks.tracker.task.model.dto.TaskApiResponse;
import com.family_tasks.tracker.task.model.dto.TaskFilterRequest;
import com.family_tasks.tracker.task.model.entity.TaskEntity;
import com.family_tasks.tracker.task.model.mapper.TaskGetMapper;
import com.family_tasks.tracker.user.infrastructure.UserRepository;
import com.family_tasks.tracker.user.model.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.family_tasks.tracker.common.validation.ValidationMessage.USER_NOT_EXIST;

@Service
@RequiredArgsConstructor
public class TaskGetService {

    private final TaskRepository taskRepository;
    private final TaskGetMapper mapper;
    private final TaskValidateService validateService;
    private final UserRepository userRepository;

    public TaskApiResponse getTask(String id, Integer userId) {

        Optional<TaskEntity> fromDB = taskRepository.findById(id);
        TaskEntity taskEntity = fromDB.orElseThrow(() -> NotFoundException.taskNotFound(id));

        validateService.validateTaskGetting(userId, taskEntity);

        return mapper.toResponse(taskEntity);
    }

    public List<TaskApiResponse> getTasks(TaskFilterRequest taskFilterRequest) {

        UserEntity user = userRepository.findById(taskFilterRequest.getUserId())
                .orElseThrow(() -> new IllegalArgumentException(String.format(USER_NOT_EXIST, taskFilterRequest.getUserId())));
        Integer groupId = user.getGroupId();

        List<TaskEntity> tasks = switch (taskFilterRequest.getFilter()) {
            case ALL_AVAILABLE -> taskRepository.findAllTasksWithoutFilters(user.getId(), groupId);
            case ALL_CLOSED -> taskRepository.findAllClosedTasks(groupId);
            case IS_REPORTER_ACTIVE_TASK -> taskRepository.findTasksWhereUserIsReporterAndTasksActive(user.getId());
            case IS_REPORTER_COMPLETED_TASK ->
                    taskRepository.findTasksWhereUserIsReporterAndTasksCompleted(user.getId());
            case IS_EXECUTOR_ACTIVE_TASK -> taskRepository.findTasksWhereUserIsExecutorAndTasksActive(user.getId());
            case IS_EXECUTOR_COMPLETED_TASK ->
                    taskRepository.findTasksWhereUserIsExecutorAndTasksCompleted(user.getId());
        };
        return tasks.stream().map(mapper::toResponse).toList();
    }
}