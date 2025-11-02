package com.family_tasks.tracker.task.core;

import com.family_tasks.tracker.common.error.exception.NotFoundException;
import com.family_tasks.tracker.task.infrastructure.TaskRepository;
import com.family_tasks.tracker.task.model.dto.TaskCreateApiRequest;
import com.family_tasks.tracker.task.model.dto.TaskUpdateApiRequest;
import com.family_tasks.tracker.task.model.entity.TaskEntity;
import com.family_tasks.tracker.user.infrastructure.UserRepository;
import com.family_tasks.tracker.user.model.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static com.family_tasks.tracker.common.validation.ValidationMessage.*;

@Service
public class TaskValidateService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskRepository taskRepository;

    void validateTaskCreation(TaskCreateApiRequest apiRequest) {
        UserEntity reporter = userRepository.findById(apiRequest.getReporterId())
                .orElseThrow(() -> new IllegalArgumentException(String.format(USER_NOT_EXIST, apiRequest.getReporterId())));

        if (reporter.getGroupId() == null) {
            throw new IllegalArgumentException(CREATE_TASK_WITHOUT_GROUP);
        }
        checkUserGroup(apiRequest.getExecutorIds(), reporter.getGroupId());
    }

    void validateTaskUpdating(TaskUpdateApiRequest apiRequest, String id) {
        TaskEntity taskEntity = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(String.format(TASK_NOT_EXIST, id)));

        Integer reporterId = taskEntity.getReporterId();
        UserEntity reporter = userRepository.findById(reporterId)
                .orElseThrow(() -> new IllegalArgumentException(String.format(USER_NOT_EXIST, reporterId)));

        Integer groupId = reporter.getGroupId();
        Set<Integer> userIds = new HashSet<>(apiRequest.getExecutorIds());

        checkUserGroup(userIds, groupId);
    }

    void validateTaskGetting(Integer userId, TaskEntity taskEntity) {
        validateUserExisting(userId);

        Set<Integer> userIds = new HashSet<>();
        userIds.add(taskEntity.getReporterId());
        userIds.addAll(taskEntity.getExecutorIds());

        if (taskEntity.isConfidential() && !userIds.contains(userId)) {
            throw NotFoundException.taskNotFound(taskEntity.getId());
        }
    }

    void validateUserExisting(Integer userId) {
        if (userId == null) {
            throw new IllegalArgumentException(USER_NOT_SPECIFIED);
        }

        if (!userRepository.existsById(userId)) {
            throw NotFoundException.userNotFound(userId);
        }
    }

    private void checkUserGroup(Set<Integer> executorIds, Integer groupId) {
        for (Integer userId : executorIds) {
            UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException(String.format(USER_NOT_EXIST, userId)));
            if (!Objects.equals(user.getGroupId(), groupId)) {
                throw new IllegalArgumentException(CREATE_OR_UPDATE_TASK_FOR_OWN_GROUP);
            }
        }
    }
}