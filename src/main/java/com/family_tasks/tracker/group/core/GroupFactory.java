package com.family_tasks.tracker.group.core;

import com.family_tasks.tracker.group.model.dto.GroupCreateApiRequest;
import com.family_tasks.tracker.group.model.entity.GroupEntity;
import com.family_tasks.tracker.group.model.mapper.GroupCreateMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GroupFactory {

    private final GroupCreateMapper mapper;

    public GroupEntity createGroup(GroupCreateApiRequest apiRequest) {
        log.info("Creating a new group. Request: {}", apiRequest);
        GroupEntity entity = mapper.toEntity(apiRequest);
        log.info("Creating a new group. Request: {}", entity);
        return entity;
    }
}