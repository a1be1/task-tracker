package com.family_tasks.tracker.user.application;

import com.family_tasks.tracker.AbstractIntegrationTest;
import com.family_tasks.tracker.common.error.ErrorResponse;
import com.family_tasks.tracker.common.utils.TimeUtils;
import com.family_tasks.tracker.group.infrastructure.GroupRepository;
import com.family_tasks.tracker.group.model.entity.GroupEntity;
import com.family_tasks.tracker.user.infrastructure.UserRepository;
import com.family_tasks.tracker.user.model.dto.UserCreateApiRequest;
import com.family_tasks.tracker.user.model.dto.UserApiResponse;
import com.family_tasks.tracker.user.model.entity.UserEntity;
import com.family_tasks.tracker.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static com.family_tasks.tracker.common.validation.ValidationConstants.USER_NAME_MAX_LENGTH;
import static com.family_tasks.tracker.common.validation.ValidationMessage.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link UserController}
 */
class UserCreateControllerTest extends AbstractIntegrationTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    GroupRepository groupRepository;

    @Test
    void withoutGroup_createUser() {
        //prepare
        UserCreateApiRequest request = buildRequest().build();
        //execute
        ResponseEntity<UserApiResponse> responseEntity = client.postForEntity(UserController.USER_URL, request, UserApiResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        UserApiResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isNotNull();
        assertThat(response.getName()).isEqualTo(request.getName());
        assertThat(response.isAdmin()).isEqualTo(request.getAdmin());
        assertThat(response.getGroupId()).isEqualTo(request.getGroupId());

        Integer userId = response.getUserId();
        assertThat(userRepository.findById(userId)).isNotNull();
    }

    @Test
    void withGroup_createUser() {
        //prepare
        Integer ownerId = createUser();
        Integer groupId = createGroup(ownerId);
        UserCreateApiRequest request = buildRequest()
                .groupId(groupId)
                .build();
        //execute
        ResponseEntity<UserApiResponse> responseEntity = client.postForEntity(UserController.USER_URL, request, UserApiResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        UserApiResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isNotNull();
        assertThat(response.getName()).isEqualTo(request.getName());
        assertThat(response.isAdmin()).isEqualTo(request.getAdmin());
        assertThat(response.getGroupId()).isEqualTo(request.getGroupId());

        Integer userId = response.getUserId();
        assertThat(userRepository.findById(userId)).isNotNull();
    }

    @Test
    void invalidUserName_createUser() {
        //prepare
        UserCreateApiRequest request = buildRequest()
                .name(TestUtils.randomString(USER_NAME_MAX_LENGTH + 1))
                .build();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.postForEntity(UserController.USER_URL, request, ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(USER_NAME_TOO_LONG);
    }

    @Test
    void emptyUserName_createUser() {
        //prepare
        UserCreateApiRequest request = buildRequest()
                .name(null)
                .build();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.postForEntity(UserController.USER_URL, request, ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(USER_NAME_NOT_SPECIFIED);
    }

    @Test
    void emptyIsAdmin_createUser() {
        //prepare
        UserCreateApiRequest request = buildRequest()
                .admin(null)
                .build();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.postForEntity(UserController.USER_URL, request, ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(IS_ADMIN_NOT_SPECIFIED);
    }

    @Test
    void whenGroupNotExist_createUser() {
        //prepare
        Integer ownerId = createUser();
        Integer groupId = createGroup(ownerId) + 2;
        UserCreateApiRequest request = buildRequest()
                .groupId(groupId)
                .build();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.postForEntity(UserController.USER_URL, request, ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(String.format(GROUP_NOT_EXIST, groupId));
    }

    private UserCreateApiRequest.UserCreateApiRequestBuilder buildRequest() {
        return UserCreateApiRequest.builder()
                .name("user1")
                .admin(true)
                .groupId(null);
    }

    private Integer createGroup(Integer ownerId) {
        GroupEntity groupEntity = GroupEntity.builder()
                .ownerId(ownerId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deletedAt(null)
                .build();
        groupRepository.save(groupEntity);
        return groupEntity.getId();
    }

    private Integer createUser() {
        UserEntity userEntity = new UserEntity();
        userEntity.setName("user name");
        userEntity.setAdmin(false);
        userEntity.setGroupId(null);
        userEntity.setCreatedAt(TimeUtils.now());
        userEntity.setUpdatedAt(TimeUtils.now());
        userRepository.save(userEntity);
        return userEntity.getId();
    }
}