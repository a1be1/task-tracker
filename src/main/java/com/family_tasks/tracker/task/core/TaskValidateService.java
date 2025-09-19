package com.family_tasks.tracker.task.core;

import com.family_tasks.tracker.common.error.exception.NotFoundException;
import com.family_tasks.tracker.task.model.dto.TaskCreateApiRequest;
import com.family_tasks.tracker.task.model.dto.TaskUpdateApiRequest;
import com.family_tasks.tracker.task.model.entity.TaskEntity;
import com.family_tasks.tracker.user.infrastructure.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

import static com.family_tasks.tracker.common.validation.ValidationMessage.USER_NOT_EXIST;
import static com.family_tasks.tracker.common.validation.ValidationMessage.USER_NOT_SPECIFIED;

@Service
public class TaskValidateService {

    @Autowired
    private UserRepository userRepository;

    void validateTaskCreation(TaskCreateApiRequest apiRequest) {
        Set<Integer> userIds = new HashSet<>();
        userIds.add(apiRequest.getReporterId());
        userIds.addAll(apiRequest.getExecutorIds());

        for (Integer id : userIds) {
            if (!userRepository.existsById(id)) {
                throw new IllegalArgumentException(String.format(USER_NOT_EXIST, id));
            }
        }
    }

    void validateTaskUpdating(TaskUpdateApiRequest apiRequest) {
        for (Integer id : apiRequest.getExecutorIds()) {
            if (!userRepository.existsById(id)) {
                throw new IllegalArgumentException(String.format(USER_NOT_EXIST, id));
            }
        }
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
}