package com.family_tasks.tracker.task.core;

import com.family_tasks.tracker.reward.core.RewardAccrualService;
import com.family_tasks.tracker.reward.infrastructure.RewardRepository;
import com.family_tasks.tracker.reward.model.dto.RewardAccrualRequest;
import com.family_tasks.tracker.task.infrastructure.TaskRepository;
import com.family_tasks.tracker.task.model.dto.TaskApiResponse;
import com.family_tasks.tracker.task.model.dto.TaskUpdateApiRequest;
import com.family_tasks.tracker.task.model.entity.TaskEntity;
import com.family_tasks.tracker.task.model.enums.TaskStatus;
import com.family_tasks.tracker.task.model.mapper.TaskUpdateMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

import static com.family_tasks.tracker.common.validation.ValidationMessage.TASK_NOT_EXIST;

@Service
@RequiredArgsConstructor
public class TaskUpdateService {

    private final TaskRepository taskRepository;
    private final TaskUpdateMapper mapper;
    private final TaskValidateService validateService;
    private final RewardAccrualService rewardAccrualService;
    private final RewardRepository rewardRepository;

    public TaskApiResponse updateTask(String id, TaskUpdateApiRequest apiRequest) {
        validateService.validateTaskUpdating(apiRequest, id);
        Optional<TaskEntity> fromDB = taskRepository.findById(id);

        return fromDB.map(task -> {
                    mapper.fillWithRequest(task, apiRequest);
                    taskRepository.save(task);
                    rewardsAccrual(task);
                    return mapper.toResponse(task);
                }
        ).orElseThrow(() -> new IllegalArgumentException(String.format(TASK_NOT_EXIST, id)));
    }

    private void rewardsAccrual(TaskEntity task) {
        if (Objects.equals(task.getStatus(), TaskStatus.COMPLETED.name())
                && task.getRewardsPoints() != null
                && task.getExecutorIds() != null) {
            task.getExecutorIds().stream()
                    .map(executorId -> rewardAccrualRequest(executorId, task))
                    .forEach(rewardAccrualService::accrualReward);
        }
    }

    private RewardAccrualRequest rewardAccrualRequest(Integer userId, TaskEntity task) {
        return RewardAccrualRequest.builder()
                .taskId(task.getId())
                .userId(userId)
                .amount(task.getRewardsPoints())
                .build();
    }
}