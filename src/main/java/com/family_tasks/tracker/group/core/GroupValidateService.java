package com.family_tasks.tracker.group.core;

import com.family_tasks.tracker.group.infrastructure.GroupRepository;
import com.family_tasks.tracker.group.model.dto.GroupCreateApiRequest;
import com.family_tasks.tracker.user.infrastructure.UserRepository;
import com.family_tasks.tracker.user.model.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.family_tasks.tracker.common.validation.ValidationMessage.*;

@Service
public class GroupValidateService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GroupRepository groupRepository;

    void validateGroupCreation(GroupCreateApiRequest apiRequest) {
        Integer userId = apiRequest.getOwnerId();

        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException(String.format(USER_NOT_EXIST, userId));
        }

        if (groupRepository.existsByOwnerId(userId)) {
            throw new IllegalArgumentException(String.format(USER_ALREADY_IS_OWNER, userId));
        }

        UserEntity user = userRepository.findById(userId).orElseThrow();
        if (user.getGroupId() != null) {
            throw new IllegalArgumentException(USER_ALREADY_HAS_GROUP);
        }
    }
}