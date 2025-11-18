package com.family_tasks.tracker.user.application;

import com.family_tasks.tracker.AbstractIntegrationTest;
import com.family_tasks.tracker.common.error.ErrorResponse;
import com.family_tasks.tracker.group.infrastructure.GroupRepository;
import com.family_tasks.tracker.user.infrastructure.UserRepository;
import com.family_tasks.tracker.user.model.dto.UserApiResponse;
import com.family_tasks.tracker.user.model.dto.UserUpdateApiRequest;
import com.family_tasks.tracker.user.model.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static com.family_tasks.tracker.common.validation.ValidationMessage.*;
import static com.family_tasks.tracker.utils.TestUtils.randomBoolean;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link UserController}
 */
public class UserUpdateControllerTest extends AbstractIntegrationTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    GroupRepository groupRepository;

    @Test
    void whenUserExist_updateUser() {
        //prepare
        UserEntity userEntity = createUserEntity();
        Integer userId = userEntity.getId();
        Integer groupId = createGroupEntity(userId);
        UserUpdateApiRequest request = buildUpdateRequest(groupId).build();
        //execute
        ResponseEntity<UserApiResponse> responseEntity = client.exchange(UserController.USER_URL + "/" + userId,
                HttpMethod.PUT,
                new HttpEntity<>(request),
                UserApiResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        UserApiResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo(request.getName());
        assertThat(response.getGroupId()).isEqualTo(request.getGroupId());
        assertThat(response.isAdmin()).isEqualTo(request.getAdmin());
    }

    @Test
    void whenUserNotExist_updateUser() {
        //prepare
        UserEntity userEntity = createUserEntity();
        Integer userId = userEntity.getId() + 10;
        Integer groupId = createGroupEntity(userEntity.getId());
        UserUpdateApiRequest request = buildUpdateRequest(groupId)
                .build();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.exchange(UserController.USER_URL + "/" + userId,
                HttpMethod.PUT,
                new HttpEntity<>(request),
                ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(String.format(USER_NOT_EXIST, userId));
    }

    @Test
    void whenUserIsNull_updateUser() {
        //prepare
        UserEntity userEntity = createUserEntity();
        Integer userId = null;
        Integer groupId = createGroupEntity(userEntity.getId());
        UserUpdateApiRequest request = buildUpdateRequest(groupId)
                .build();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.exchange(UserController.USER_URL + "/" + userId,
                HttpMethod.PUT,
                new HttpEntity<>(request),
                ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(ID_HAS_INVALID_FORMAT);
    }


    @Test
    void whenNameIsNull_updateUser() {
        //prepare
        UserEntity userEntity = createUserEntity();
        Integer userId = userEntity.getId();
        Integer groupId = createGroupEntity(userId);
        UserUpdateApiRequest request = buildUpdateRequest(groupId)
                .name(null)
                .build();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.exchange(UserController.USER_URL + "/" + userId,
                HttpMethod.PUT,
                new HttpEntity<>(request),
                ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(USER_NAME_NOT_SPECIFIED);
    }

    @Test
    void whenNameIsEmpty_updateUser() {
        //prepare
        UserEntity userEntity = createUserEntity();
        Integer userId = userEntity.getId();
        Integer groupId = createGroupEntity(userId);
        UserUpdateApiRequest request = buildUpdateRequest(groupId)
                .name("")
                .build();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.exchange(UserController.USER_URL + "/" + userId,
                HttpMethod.PUT,
                new HttpEntity<>(request),
                ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(USER_NAME_NOT_SPECIFIED);
    }

    @Test
    void whenAdminIsNull_updateUser() {
        //prepare
        UserEntity userEntity = createUserEntity();
        Integer userId = userEntity.getId();
        Integer groupId = createGroupEntity(userId);
        UserUpdateApiRequest request = buildUpdateRequest(groupId)
                .admin(null)
                .build();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.exchange(UserController.USER_URL + "/" + userId,
                HttpMethod.PUT,
                new HttpEntity<>(request),
                ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(IS_ADMIN_NOT_SPECIFIED);
    }

    @Test
    void whenGroupNotExist_updateUser() {
        //prepare
        UserEntity userEntity = createUserEntity();
        Integer userId = userEntity.getId();
        Integer groupId = createGroupEntity(userId) + 10;
        UserUpdateApiRequest request = buildUpdateRequest(groupId)
                .groupId(groupId)
                .build();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.exchange(UserController.USER_URL + "/" + userId,
                HttpMethod.PUT,
                new HttpEntity<>(request),
                ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(String.format(GROUP_NOT_EXIST, groupId));
    }

    private UserUpdateApiRequest.UserUpdateApiRequestBuilder buildUpdateRequest(Integer groupId) {
        return UserUpdateApiRequest.builder()
                .name("Name after update")
                .groupId(groupId)
                .admin(randomBoolean());
    }
}