package com.family_tasks.tracker.group.application;

import com.family_tasks.tracker.AbstractIntegrationTest;
import com.family_tasks.tracker.common.error.ErrorResponse;
import com.family_tasks.tracker.group.infrastructure.GroupRepository;
import com.family_tasks.tracker.group.model.dto.GroupApiResponse;
import com.family_tasks.tracker.group.model.dto.GroupCreateApiRequest;
import com.family_tasks.tracker.group.model.entity.GroupEntity;
import com.family_tasks.tracker.user.infrastructure.UserRepository;
import com.family_tasks.tracker.user.model.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static com.family_tasks.tracker.common.validation.ValidationMessage.*;
import static com.family_tasks.tracker.group.application.GroupController.GROUP_URL;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link GroupController}
 */
public class GroupCreateControllerTest extends AbstractIntegrationTest {
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    UserRepository userRepository;

    @Test
    void createGroup() {
        //prepare
        Integer ownerId = createUserEntity().getId();
        GroupCreateApiRequest request = GroupCreateApiRequest.builder()
                .ownerId(ownerId)
                .build();
        //execute
        ResponseEntity<GroupApiResponse> responseEntity = client.postForEntity(GROUP_URL, request, GroupApiResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        GroupApiResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.getGroupId()).isNotNull();
        assertThat(response.getOwnerId()).isEqualTo(request.getOwnerId());
        assertThat(response.getGroupId()).isEqualTo(userRepository.findById(ownerId).orElseThrow().getGroupId());

        GroupEntity groupEntity = groupRepository.findById(response.getGroupId()).orElseThrow();
        assertThat(groupEntity).isNotNull();
    }

    @Test
    void whenUserIsMemberOfAnotherGroup_createGroup() {
        //prepare
        UserEntity owner = createUserEntity();
        Integer groupId = createGroupEntity(owner.getId());
        owner.setGroupId(groupId);
        userRepository.save(owner);

        UserEntity user = createUserEntity();
        user.setGroupId(groupId);
        userRepository.save(user);

        GroupCreateApiRequest request = GroupCreateApiRequest.builder()
                .ownerId(user.getId())
                .build();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.postForEntity(GROUP_URL, request, ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(USER_ALREADY_HAS_GROUP);
    }

    @Test
    void whenOwnerNull_createGroup() {
        //prepare
        GroupCreateApiRequest request = GroupCreateApiRequest.builder()
                .ownerId(null)
                .build();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.postForEntity(GROUP_URL, request, ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(GROUP_OWNER_NOT_SPECIFIED);
    }

    @Test
    void whenOwnerAlreadyHasGroup_createGroup() {
        //prepare
        UserEntity owner = createUserEntity();
        Integer groupId = createGroupEntity(owner.getId());
        owner.setGroupId(groupId);
        userRepository.save(owner);


        GroupCreateApiRequest request = GroupCreateApiRequest.builder()
                .ownerId(owner.getId())
                .build();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.postForEntity(GROUP_URL, request, ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(String.format(USER_ALREADY_IS_OWNER, owner.getId()));
    }
}