package com.family_tasks.tracker.group.core;

import com.family_tasks.tracker.group.infrastructure.GroupRepository;
import com.family_tasks.tracker.group.model.dto.GroupApiResponse;
import com.family_tasks.tracker.group.model.dto.GroupCreateApiRequest;
import com.family_tasks.tracker.group.model.entity.GroupEntity;
import com.family_tasks.tracker.group.model.mapper.GroupCreateMapper;
import com.family_tasks.tracker.user.infrastructure.UserRepository;
import com.family_tasks.tracker.user.model.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GroupCreateService {
    private final GroupRepository groupRepository;
    private final GroupFactory groupFactory;
    private final GroupCreateMapper mapper;
    private final GroupValidateService validateService;
    private final UserRepository userRepository;

    public GroupApiResponse createGroup(GroupCreateApiRequest apiRequest) {

        validateService.validateGroupCreation(apiRequest);
        GroupEntity groupEntity = groupFactory.createGroup(apiRequest);
        groupRepository.save(groupEntity);

        UserEntity owner = userRepository.findById(apiRequest.getOwnerId()).orElseThrow();
        owner.setGroupId(groupEntity.getId());
        userRepository.save(owner);

        return mapper.toResponse(groupEntity);
    }
}