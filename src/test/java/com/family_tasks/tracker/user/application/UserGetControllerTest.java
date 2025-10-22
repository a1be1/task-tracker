package com.family_tasks.tracker.user.application;

import com.family_tasks.tracker.AbstractIntegrationTest;
import com.family_tasks.tracker.user.infrastructure.UserRepository;
import com.family_tasks.tracker.user.model.dto.UserApiResponse;
import com.family_tasks.tracker.user.model.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test for {@link UserController}
 */
public class UserGetControllerTest extends AbstractIntegrationTest {
    @Autowired
    UserRepository userRepository;

    @Test
    void whenUserExist_getUser() {
        //prepare
        UserEntity userEntity = buildUserEntity();
        userRepository.save(userEntity);
        Integer userId = userEntity.getId();
        Integer requestingUserId = createRequestingUser();
        String url = UriComponentsBuilder
                .fromUriString(UserController.USER_URL + "/" + userId)
                .queryParam("requestingUserId", requestingUserId)
                .toString();
        //execute
        ResponseEntity<UserApiResponse> responseEntity = client.getForEntity(url, UserApiResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        UserApiResponse response = responseEntity.getBody();
        assertThat(response).isEqualTo(toApiResponse(userEntity));
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

    private Integer createRequestingUser() {
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
}
