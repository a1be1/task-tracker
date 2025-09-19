package com.family_tasks.tracker.task.application;

import com.family_tasks.tracker.AbstractIntegrationTest;
import com.family_tasks.tracker.common.error.ErrorResponse;
import com.family_tasks.tracker.common.utils.TimeUtils;
import com.family_tasks.tracker.task.infrastructure.TaskRepository;
import com.family_tasks.tracker.task.model.dto.TaskApiResponse;
import com.family_tasks.tracker.task.model.dto.TaskCreateApiRequest;
import com.family_tasks.tracker.task.model.entity.TaskEntity;
import com.family_tasks.tracker.task.model.enums.TaskPriority;
import com.family_tasks.tracker.task.model.enums.TaskStatus;
import com.family_tasks.tracker.user.infrastructure.UserRepository;
import com.family_tasks.tracker.user.model.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Set;

import static com.family_tasks.tracker.common.validation.ValidationConstants.*;
import static com.family_tasks.tracker.common.validation.ValidationMessage.*;
import static com.family_tasks.tracker.task.application.TaskController.TASK_URL;
import static com.family_tasks.tracker.utils.TestUtils.randomString;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link TaskController}
 */
public class TaskCreateControllerTest extends AbstractIntegrationTest {

    @Autowired
    TaskRepository taskRepository;
    @Autowired
    UserRepository userRepository;

    @Test
    void createTask() {
        //prepare
        TaskCreateApiRequest request = buildCreateRequest().build();
        //execute
        ResponseEntity<TaskApiResponse> responseEntity = client.postForEntity(TASK_URL, request, TaskApiResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        TaskApiResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.getTaskId()).isNotNull();
        assertThat(response.getStatus()).isEqualTo(TaskStatus.TO_DO);
        assertThat(response.getName()).isEqualTo(request.getName());
        assertThat(response.getDescription()).isEqualTo(request.getDescription());
        assertThat(response.getPriority().name()).isEqualTo(request.getPriority());
        assertThat(response.getReporterId()).isEqualTo(request.getReporterId());
        assertThat(response.getExecutorIds()).isEqualTo(request.getExecutorIds());
        assertThat(response.isConfidential()).isEqualTo(request.getConfidential());
        assertThat(response.getDeadline()).isEqualTo(request.getDeadline());

        TaskEntity taskEntity = taskRepository.findById(response.getTaskId()).orElseThrow();
        assertThat(taskEntity).isNotNull();
    }

    @Test
    void whenPriorityInvalid_createTask() {
        //prepare
        TaskCreateApiRequest request = buildCreateRequest()
                .priority("INVALID_PRIORITY")
                .build();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.postForEntity(TASK_URL, request, ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(TASK_PRIORITY_INVALID);
    }

    @Test
    void whenPriorityNull_createTask() {
        //prepare
        TaskCreateApiRequest request = buildCreateRequest()
                .priority(null)
                .build();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.postForEntity(TASK_URL, request, ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(TASK_PRIORITY_NULL);
    }

    @Test
    void whenPriorityEmpty_createTask() {
        //prepare
        TaskCreateApiRequest request = buildCreateRequest()
                .priority("")
                .build();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.postForEntity(TASK_URL, request, ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(TASK_PRIORITY_NULL);
    }

    @Test
    void whenEmptyTaskName_createTask() {
        //prepare
        TaskCreateApiRequest request = buildCreateRequest()
                .name(null)
                .build();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.postForEntity(TASK_URL, request, ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(TASK_NAME_NOT_SPECIFIED);
    }

    @Test
    void whenTaskNameToLong_createTask() {
        TaskCreateApiRequest request = buildCreateRequest()
                .name(randomString(TASK_NAME_MAX_LENGTH + 1))
                .build();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.postForEntity(TASK_URL, request, ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(TASK_NAME_TOO_LONG);
    }

    @Test
    void whenTaskDescriptionToLong_createTask() {
        TaskCreateApiRequest request = buildCreateRequest()
                .description(randomString(TASK_DESCRIPTION_MAX_LENGTH + 1))
                .build();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.postForEntity(TASK_URL, request, ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(TASK_DESCRIPTION_TOO_LONG);
    }

    @Test
    void whenTaskDescriptionToShort_createTask() {
        TaskCreateApiRequest request = buildCreateRequest()
                .description(randomString(TASK_DESCRIPTION_MIN_LENGTH - 1))
                .build();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.postForEntity(TASK_URL, request, ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(TASK_DESCRIPTION_TOO_SHORT);
    }

    @Test
    void whenDeadlineDateInvalid_createTask() {
        //prepare
        TaskCreateApiRequest request = buildCreateRequest()
                .deadline(LocalDate.now())
                .build();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.postForEntity(TASK_URL, request, ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(TASK_DEADLINE_DATE_NOT_FUTURE);
    }

    @Test
    void whenTaskConfidentialNull_createTask() {
        //prepare
        TaskCreateApiRequest request = buildCreateRequest()
                .confidential(null)
                .build();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.postForEntity(TASK_URL, request, ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(TASK_CONFIDENTIAL_STATUS_NOT_SPECIFIED);
    }

    @Test
    void whenTaskReporterIdNull_createTask() {
        //prepare
        TaskCreateApiRequest request = buildCreateRequest()
                .reporterId(null)
                .build();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.postForEntity(TASK_URL, request, ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(TASK_REPORTER_NULL);
    }

    @Test
    void whenTaskReporterNotExist_createTask() {
        //prepare
        Integer reporterId = createUser() + 2;
        TaskCreateApiRequest request = buildCreateRequest()
                .reporterId(reporterId)
                .build();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.postForEntity(TASK_URL, request, ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(String.format(USER_NOT_EXIST, reporterId));
    }

    @Test
    void whenTaskExecutorNotExist_createTask() {
        //prepare
        Integer executorId = createUser() + 2;
        TaskCreateApiRequest request = buildCreateRequest()
                .executorIds(Set.of(executorId))
                .build();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.postForEntity(TASK_URL, request, ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(String.format(USER_NOT_EXIST, executorId));
    }

    private TaskCreateApiRequest.TaskCreateApiRequestBuilder buildCreateRequest() {
        return TaskCreateApiRequest.builder()
                .name("Name of task")
                .description("Description of task")
                .priority(TaskPriority.HIGH.name())
                .reporterId(createUser())
                .executorIds(Set.of())
                .confidential(true)
                .deadline(LocalDate.now().plusDays(1));
    }

    private Integer createUser() {
        UserEntity userEntity = new UserEntity();
        userEntity.setName("user name");
        userEntity.setAdmin(false);
        userEntity.setCreatedAt(TimeUtils.now());
        userEntity.setUpdatedAt(TimeUtils.now());
        userRepository.save(userEntity);
        return userEntity.getId();
    }
}