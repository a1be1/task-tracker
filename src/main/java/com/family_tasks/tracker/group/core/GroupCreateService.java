package com.family_tasks.tracker.group.core;

import com.family_tasks.tracker.group.infrastructure.GroupRepository;
import com.family_tasks.tracker.group.model.dto.GroupApiResponse;
import com.family_tasks.tracker.group.model.dto.GroupCreateApiRequest;
import com.family_tasks.tracker.group.model.entity.GroupEntity;
import com.family_tasks.tracker.group.model.mapper.GroupCreateMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GroupCreateService {
    private final GroupRepository groupRepository;
    private final GroupFactory groupFactory;
    private final GroupCreateMapper mapper;
    private final GroupValidateService validateService;

    public GroupApiResponse createGroup(GroupCreateApiRequest apiRequest) {
        validateService.validateGroupCreation(apiRequest);
        GroupEntity groupEntity = groupFactory.createGroup(apiRequest);
        groupRepository.save(groupEntity);
        return mapper.toResponse(groupEntity);
    }
}