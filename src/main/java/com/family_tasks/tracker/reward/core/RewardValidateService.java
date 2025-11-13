package com.family_tasks.tracker.reward.core;

import com.family_tasks.tracker.common.error.exception.NotFoundException;
import com.family_tasks.tracker.reward.model.entity.RewardEntity;
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

        if (!userRepository.existsById(userId)) {
            throw NotFoundException.userNotFound(userId);
        }
    }

    void validateRewardUpdating(Integer validatedBy, RewardEntity rewardEntity) {

        UserEntity admin = userRepository.findById(validatedBy).orElseThrow(() -> NotFoundException.userNotFound(validatedBy));
        UserEntity user = userRepository.findById(rewardEntity.getUserId()).orElseThrow(() -> NotFoundException.userNotFound(rewardEntity.getUserId()));

        if (!admin.isAdmin()) {
            throw new IllegalArgumentException(USER_NOT_ADMIN);
        }

        if (!Objects.equals(admin.getGroupId(), user.getGroupId())) {
            throw new IllegalArgumentException(String.format(REWARD_NOT_EXIST, rewardEntity.getId()));
        }
    }
}