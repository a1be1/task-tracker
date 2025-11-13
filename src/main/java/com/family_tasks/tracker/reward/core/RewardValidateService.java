package com.family_tasks.tracker.reward.core;

import com.family_tasks.tracker.common.error.exception.NotFoundException;
import com.family_tasks.tracker.user.infrastructure.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.family_tasks.tracker.common.validation.ValidationMessage.USER_NOT_SPECIFIED;

@Service
public class RewardValidateService {
    @Autowired
    private UserRepository userRepository;

    void validateUserExisting(Integer userId) {

        if (!userRepository.existsById(userId)) {
            throw NotFoundException.userNotFound(userId);
        }
    }
}