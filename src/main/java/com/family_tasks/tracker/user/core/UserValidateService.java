package com.family_tasks.tracker.user.core;

import com.family_tasks.tracker.group.infrastructure.GroupRepository;
import com.family_tasks.tracker.user.model.dto.CreateUserApiRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.family_tasks.tracker.common.validation.ValidationMessage.GROUP_NOT_EXIST;

@Service
public class UserValidateService {
    @Autowired
    private GroupRepository groupRepository;

    void validateUserCreation(CreateUserApiRequest apiRequest) {
        Integer groupId = apiRequest.getGroupId();
        if (groupId!=null && !groupRepository.existByGroupId(groupId)) {
            throw new IllegalArgumentException(String.format(GROUP_NOT_EXIST, groupId));
        }
    }
}