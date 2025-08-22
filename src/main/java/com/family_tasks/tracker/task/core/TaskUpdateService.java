package com.family_tasks.tracker.task.core;

import com.family_tasks.tracker.task.infrastructure.TaskRepository;
import com.family_tasks.tracker.task.model.dto.TaskApiResponse;
import com.family_tasks.tracker.task.model.dto.TaskUpdateApiRequest;
import com.family_tasks.tracker.task.model.entity.TaskEntity;
import com.family_tasks.tracker.task.model.mupper.TaskUpdateMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Optional;

import static com.family_tasks.tracker.common.validation.ValidationMessage.TASK_NOT_EXIST;

@Service
@RequiredArgsConstructor
public class TaskUpdateService {

    private final TaskRepository taskRepository;
    private final TaskUpdateMapper mapper;
    private final TaskValidateService validateService;

    public TaskApiResponse updateTask(String id, TaskUpdateApiRequest apiRequest) {
        validateService.validateTaskUpdating(apiRequest);
        Optional<TaskEntity> fromDB = taskRepository.findById(id);

        return fromDB.map(task -> {
                    mapper.fillWithRequest(task, apiRequest);
                    taskRepository.save(task);
                    return mapper.toResponse(task);
                }
        ).orElseThrow(() -> new IllegalArgumentException(String.format(TASK_NOT_EXIST, id)));
    }
}