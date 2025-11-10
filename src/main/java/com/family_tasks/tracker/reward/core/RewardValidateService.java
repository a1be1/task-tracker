package com.family_tasks.tracker.reward.core;

import com.family_tasks.tracker.common.error.exception.NotFoundException;
import com.family_tasks.tracker.user.infrastructure.UserRepository;
import com.family_tasks.tracker.user.model.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.family_tasks.tracker.common.validation.ValidationMessage.*;

@Service
public class RewardValidateService {
    @Autowired
    private UserRepository userRepository;

    void validateUserExisting(Integer userId) {
        if (userId == null) {
            throw new IllegalArgumentException(USER_NOT_SPECIFIED);
        }

        if (!userRepository.existsById(userId)) {
            throw NotFoundException.userNotFound(userId);
        }
    }

    void validateRewardUpdating(Integer validatedBy, Integer userId) {

        UserEntity admin = userRepository.findById(validatedBy).orElseThrow(() -> NotFoundException.userNotFound(validatedBy));
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> NotFoundException.userNotFound(userId));

        if (!admin.isAdmin()) {
            throw new IllegalArgumentException(USER_NOT_ADMIN);
        }

        if (!Objects.equals(admin.getGroupId(), user.getGroupId())) {
            throw new IllegalArgumentException(UPDATE_REWARD_FOR_OWN_GROUP);
        }
    }
}