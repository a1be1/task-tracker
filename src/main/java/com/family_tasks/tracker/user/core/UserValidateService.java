package com.family_tasks.tracker.user.core;

import com.family_tasks.tracker.common.error.exception.NotFoundException;
import com.family_tasks.tracker.group.infrastructure.GroupRepository;
import com.family_tasks.tracker.user.infrastructure.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.family_tasks.tracker.common.validation.ValidationMessage.GROUP_NOT_EXIST;
import static com.family_tasks.tracker.common.validation.ValidationMessage.USER_NOT_SPECIFIED;

@Service
public class UserValidateService {
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private UserRepository userRepository;

    void validateGroupExisting(Integer groupId) {
        if (groupId != null && !groupRepository.existByGroupId(groupId)) {
            throw new IllegalArgumentException(String.format(GROUP_NOT_EXIST, groupId));
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