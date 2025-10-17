package com.family_tasks.tracker.group.core;

import com.family_tasks.tracker.group.model.dto.GroupCreateApiRequest;
import com.family_tasks.tracker.group.model.entity.GroupEntity;
import com.family_tasks.tracker.group.model.mapper.GroupCreateMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class GroupFactoryTest {
    private final GroupCreateMapper mapper = Mappers.getMapper(GroupCreateMapper.class);
    private final GroupFactory groupFactory = new GroupFactory(mapper);

    @Test
    void createGroup() {
        //prepare
        GroupCreateApiRequest apiRequest = GroupCreateApiRequest.builder()
                .ownerId(1)
                .build();
        //execute
        GroupEntity groupEntity = groupFactory.createGroup(apiRequest);
        //validate
        assertThat(groupEntity).isNotNull();
        assertThat(groupEntity.getId()).isNull();
        assertThat(groupEntity.getOwnerId()).isEqualTo(apiRequest.getOwnerId());
        Assertions.assertThat(groupEntity.getCreatedAt()).isNotNull();
        Assertions.assertThat(groupEntity.getUpdatedAt()).isNotNull();
        Assertions.assertThat(groupEntity.getDeletedAt()).isNull();
    }
}