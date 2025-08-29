package com.family_tasks.tracker.user.application;

import com.family_tasks.tracker.AbstractIntegrationTest;
import com.family_tasks.tracker.common.error.ErrorResponse;
import com.family_tasks.tracker.common.validation.ValidationMessage;
import com.family_tasks.tracker.user.infrastructure.UserRepository;
import com.family_tasks.tracker.user.model.dto.CreateUserApiRequest;
import com.family_tasks.tracker.user.model.dto.CreateUserApiResponse;
import com.family_tasks.tracker.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static com.family_tasks.tracker.common.validation.ValidationConstants.USER_NAME_MAX_LENGTH;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link UserController}
 */
class UserControllerTest extends AbstractIntegrationTest {

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

        Integer userId = response.getUserId();
        assertThat(userRepository.findById(userId)).isNotNull();
    }

    @Test
    void invalidUserName_createUser() {
        //prepare
        CreateUserApiRequest request = CreateUserApiRequest.builder()
                .name(TestUtils.randomString(USER_NAME_MAX_LENGTH + 1))
                .admin(true)
                .build();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.postForEntity(UserController.USER_URL, request, ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(ValidationMessage.USER_NAME_TOO_LONG);
    }

    @Test
    void emptyUserName_createUser() {
        //prepare
        CreateUserApiRequest request = CreateUserApiRequest.builder()
                .admin(true)
                .name(null)
                .build();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.postForEntity(UserController.USER_URL, request, ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(ValidationMessage.USER_NAME_NOT_SPECIFIED);
    }

    @Test
    void emptyIsAdmin_createUser() {
        //prepare
        CreateUserApiRequest request = CreateUserApiRequest.builder()
                .name("name")
                .admin(null)
                .build();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.postForEntity(UserController.USER_URL, request, ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(ValidationMessage.IS_ADMIN_NOT_SPECIFIED);
    }

    private CreateUserApiRequest buildRequest() {
        return CreateUserApiRequest.builder()
                .name("user1")
                .admin(true)
                .build();
    }
}