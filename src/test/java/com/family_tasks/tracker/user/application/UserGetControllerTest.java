package com.family_tasks.tracker.user.application;

import com.family_tasks.tracker.AbstractIntegrationTest;
import com.family_tasks.tracker.common.error.ErrorResponse;
import com.family_tasks.tracker.group.infrastructure.GroupRepository;
import com.family_tasks.tracker.group.model.entity.GroupEntity;
import com.family_tasks.tracker.user.infrastructure.UserRepository;
import com.family_tasks.tracker.user.model.dto.UserApiResponse;
import com.family_tasks.tracker.user.model.entity.UserEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.List;

import static com.family_tasks.tracker.common.validation.ValidationMessage.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test for {@link UserController}
 */
public class UserGetControllerTest extends AbstractIntegrationTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    GroupRepository groupRepository;

    @Test
    void whenUserExist_getUser() {
        //prepare
        UserEntity userEntity = buildUserEntity();
        userRepository.save(userEntity);
        Integer userId = userEntity.getId();
        String url = UriComponentsBuilder
                .fromUriString(UserController.USER_URL + "/" + userId)
                .build().toString();
        //execute
        ResponseEntity<UserApiResponse> responseEntity = client.getForEntity(url, UserApiResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        UserApiResponse response = responseEntity.getBody();
        assertThat(response).isEqualTo(toApiResponse(userEntity));
    }

    @Test
    void whenUserNotExist_getUser() {
        //prepare
        UserEntity userEntity = buildUserEntity();
        userRepository.save(userEntity);
        Integer userId = userEntity.getId() + 1;
        String url = UriComponentsBuilder
                .fromUriString(UserController.USER_URL + "/" + userId)
                .build().toString();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.getForEntity(url, ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        Assertions.assertNotNull(response);
        assertThat(response.errorMessage()).isEqualTo(String.format(USER_NOT_EXIST, userId));
    }

    @Test
    void whenUserIsNull_getUser() {
        //prepare
        Integer userId = null;
        String url = UriComponentsBuilder
                .fromUriString(UserController.USER_URL + "/" + userId)
                .build().toString();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.getForEntity(url, ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        Assertions.assertNotNull(response);
        assertThat(response.errorMessage()).isEqualTo(ID_HAS_INVALID_FORMAT);
    }

    @Test
    void whenUserIsGroupOwner_getAllUsers() {
        //prepare
        UserEntity groupOwner = buildUserEntity();
        userRepository.save(groupOwner);
        Integer ownerId = groupOwner.getId();
        Integer groupId = createGroup(ownerId);
        groupOwner.setGroupId(groupId);
        userRepository.save(groupOwner);
        UserEntity memberGroup = buildUserEntity();
        memberGroup.setGroupId(groupId);
        userRepository.save(memberGroup);

        String url = UriComponentsBuilder
                .fromUriString(UserController.USER_URL)
                .queryParam("groupId", groupId)
                .toUriString();
        //execute
        ResponseEntity<List<UserApiResponse>> responseEntity =
                client.exchange(
                        url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<UserApiResponse>>() {
                        }
                );
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<UserApiResponse> response = responseEntity.getBody();
        assert response != null;
        assertThat(response).containsExactlyInAnyOrderElementsOf(List.of(
                toApiResponse(groupOwner),
                toApiResponse(memberGroup)));
    }

    @Test
    void whenGroupNotExist_getAllUsers() {
        //prepare
        Integer ownerId = createUser();
        Integer groupId = createGroup(ownerId) + 1;

        String url = UriComponentsBuilder
                .fromUriString(UserController.USER_URL)
                .queryParam("groupId", groupId)
                .toUriString();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.getForEntity(url, ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(String.format(GROUP_NOT_EXIST, groupId));
    }

    @Test
    void whenGroupIsNull_getAllUsers() {
        //prepare
        Integer groupId = null;

        String url = UriComponentsBuilder
                .fromUriString(UserController.USER_URL)
                .queryParam("groupId", groupId)
                .toUriString();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.getForEntity(url, ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(ID_HAS_INVALID_FORMAT);
    }

    private UserEntity buildUserEntity() {
        UserEntity userEntity = new UserEntity();
        userEntity.setName("user name");
        userEntity.setAdmin(false);
        userEntity.setGroupId(null);
        userEntity.setCreatedAt(LocalDateTime.now());
        userEntity.setUpdatedAt(LocalDateTime.now());

        return userEntity;
    }

    private Integer createUser() {
        UserEntity userEntity = new UserEntity();
        userEntity.setName("user name");
        userEntity.setAdmin(false);
        userEntity.setGroupId(null);
        userEntity.setCreatedAt(LocalDateTime.now());
        userEntity.setUpdatedAt(LocalDateTime.now());
        userRepository.save(userEntity);
        return userEntity.getId();
    }

    private UserApiResponse toApiResponse(UserEntity userEntity) {
        return UserApiResponse.builder()
                .userId(userEntity.getId())
                .admin(userEntity.isAdmin())
                .groupId(userEntity.getGroupId())
                .name(userEntity.getName())
                .build();
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
}