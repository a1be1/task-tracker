package com.family_tasks.tracker.user.application;

import com.family_tasks.tracker.AbstractApplicationTest;
import com.family_tasks.tracker.user.infrastructure.UserRepository;
import com.family_tasks.tracker.user.model.dto.CreateUserApiRequest;
import com.family_tasks.tracker.user.model.dto.CreateUserApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link UserController}
 */
class UserControllerTest extends AbstractApplicationTest {

    @Autowired
    UserRepository userRepository;

    @Test
    void createUser() {
        //prepare
        CreateUserApiRequest request = buildRequest();
        //execute
        ResponseEntity<CreateUserApiResponse> responseEntity = client.postForEntity(UserController.USER_URL, request, CreateUserApiResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        CreateUserApiResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isNotNull();
        assertThat(response.getName()).isEqualTo(request.getName());
        assertThat(response.isAdmin()).isEqualTo(request.getAdmin());

        String userId = response.getUserId();
        assertThat(userRepository.getUser(userId)).isNotNull();
    }

    private CreateUserApiRequest buildRequest() {
        return CreateUserApiRequest.builder()
                .name("user1")
                .admin(true)
                .build();
    }
}