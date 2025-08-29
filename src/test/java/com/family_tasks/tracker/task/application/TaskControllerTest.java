package com.family_tasks.tracker.task.application;

import com.family_tasks.tracker.AbstractIntegrationTest;
import com.family_tasks.tracker.common.error.ErrorResponse;
import com.family_tasks.tracker.task.infrastructure.TaskRepository;
import com.family_tasks.tracker.task.model.dto.TaskApiResponse;
import com.family_tasks.tracker.task.model.dto.TaskCreateApiRequest;
import com.family_tasks.tracker.task.model.dto.TaskUpdateApiRequest;
import com.family_tasks.tracker.task.model.entity.TaskEntity;
import com.family_tasks.tracker.task.model.enums.Priority;
import com.family_tasks.tracker.task.model.enums.TaskStatus;
import com.family_tasks.tracker.user.infrastructure.UserRepository;
import com.family_tasks.tracker.user.model.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static com.family_tasks.tracker.common.validation.ValidationConstants.*;
import static com.family_tasks.tracker.common.validation.ValidationMessage.*;
import static com.family_tasks.tracker.task.application.TaskController.TASK_URL;
import static com.family_tasks.tracker.utils.TestUtils.randomString;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link TaskController}
 */
public class TaskControllerTest extends AbstractIntegrationTest {

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
        Integer reporterId = getUserId() + 2;
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
        Integer executorId = getUserId() + 2;
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


    @Test
    void whenTaskExist_updateTask() {
        //prepare
        TaskEntity taskEntity = buildTaskEntity();
        taskRepository.save(taskEntity);
        String taskId = taskEntity.getId();
        TaskUpdateApiRequest request = buildUpdateRequest().build();
        //execute
        ResponseEntity<TaskApiResponse> responseEntity = client.exchange(TaskController.TASK_URL + "/" + taskId, HttpMethod.PUT, new HttpEntity<>(request), TaskApiResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        TaskApiResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(TaskStatus.CANCELLED);
        assertThat(response.getName()).isEqualTo(request.getName());
        assertThat(response.getDescription()).isEqualTo(request.getDescription());
        assertThat(response.getPriority().name()).isEqualTo(request.getPriority());
        assertThat(response.getExecutorIds()).isEqualTo(request.getExecutorIds());
        assertThat(response.isConfidential()).isEqualTo(request.getConfidential());
        assertThat(response.getDeadline()).isEqualTo(request.getDeadline());
    }

    @Test
    void whenPriorityInvalid_updateTask() {
        //prepare
        TaskEntity taskEntity = buildTaskEntity();
        taskRepository.save(taskEntity);
        String taskId = taskEntity.getId();
        TaskUpdateApiRequest request = buildUpdateRequest()
                .priority("INVALID_PRIORITY")
                .build();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.exchange(TaskController.TASK_URL + "/" + taskId, HttpMethod.PUT, new HttpEntity<>(request), ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(TASK_PRIORITY_INVALID);
    }

    @Test
    void whenPriorityNull_updateTask() {
        //prepare
        TaskEntity taskEntity = buildTaskEntity();
        taskRepository.save(taskEntity);
        String taskId = taskEntity.getId();
        TaskUpdateApiRequest request = buildUpdateRequest()
                .priority(null)
                .build();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.exchange(TaskController.TASK_URL + "/" + taskId, HttpMethod.PUT, new HttpEntity<>(request), ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(TASK_PRIORITY_NULL);
    }

    @Test
    void whenPriorityEmpty_updateTask() {
        //prepare
        TaskEntity taskEntity = buildTaskEntity();
        taskRepository.save(taskEntity);
        String taskId = taskEntity.getId();
        TaskUpdateApiRequest request = buildUpdateRequest()
                .priority("")
                .build();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.exchange(TaskController.TASK_URL + "/" + taskId, HttpMethod.PUT, new HttpEntity<>(request), ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(TASK_PRIORITY_NULL);
    }

    @Test
    void whenEmptyTaskName_updateTask() {
        //prepare
        TaskEntity taskEntity = buildTaskEntity();
        taskRepository.save(taskEntity);
        String taskId = taskEntity.getId();
        TaskUpdateApiRequest request = buildUpdateRequest()
                .name("")
                .build();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.exchange(TaskController.TASK_URL + "/" + taskId, HttpMethod.PUT, new HttpEntity<>(request), ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(TASK_NAME_NOT_SPECIFIED);
    }

    @Test
    void whenTaskNameNull_updateTask() {
        //prepare
        TaskEntity taskEntity = buildTaskEntity();
        taskRepository.save(taskEntity);
        String taskId = taskEntity.getId();
        TaskUpdateApiRequest request = buildUpdateRequest()
                .name(null)
                .build();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.exchange(TaskController.TASK_URL + "/" + taskId, HttpMethod.PUT, new HttpEntity<>(request), ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(TASK_NAME_NOT_SPECIFIED);
    }

    @Test
    void whenTaskNameToLong_updateTask() {
        TaskEntity taskEntity = buildTaskEntity();
        taskRepository.save(taskEntity);
        String taskId = taskEntity.getId();
        TaskUpdateApiRequest request = buildUpdateRequest()
                .name(randomString(TASK_NAME_MAX_LENGTH + 1))
                .build();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.exchange(TaskController.TASK_URL + "/" + taskId, HttpMethod.PUT, new HttpEntity<>(request), ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(TASK_NAME_TOO_LONG);
    }

    @Test
    void whenTaskDescriptionToLong_updateTask() {
        TaskEntity taskEntity = buildTaskEntity();
        taskRepository.save(taskEntity);
        String taskId = taskEntity.getId();
        TaskUpdateApiRequest request = buildUpdateRequest()
                .description(randomString(TASK_DESCRIPTION_MAX_LENGTH + 1))
                .build();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.exchange(TaskController.TASK_URL + "/" + taskId, HttpMethod.PUT, new HttpEntity<>(request), ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(TASK_DESCRIPTION_TOO_LONG);
    }

    @Test
    void whenTaskDescriptionToShort_updateTask() {
        TaskEntity taskEntity = buildTaskEntity();
        taskRepository.save(taskEntity);
        String taskId = taskEntity.getId();
        TaskUpdateApiRequest request = buildUpdateRequest()
                .description(randomString(TASK_DESCRIPTION_MIN_LENGTH - 1))
                .build();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.exchange(TaskController.TASK_URL + "/" + taskId, HttpMethod.PUT, new HttpEntity<>(request), ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(TASK_DESCRIPTION_TOO_SHORT);
    }

    @Test
    void whenTaskConfidentialNull_updateTask() {
        //prepare
        TaskEntity taskEntity = buildTaskEntity();
        taskRepository.save(taskEntity);
        String taskId = taskEntity.getId();
        TaskUpdateApiRequest request = buildUpdateRequest()
                .confidential(null)
                .build();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.exchange(TaskController.TASK_URL + "/" + taskId, HttpMethod.PUT, new HttpEntity<>(request), ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(TASK_CONFIDENTIAL_STATUS_NOT_SPECIFIED);
    }

    @Test
    void whenStatusInvalid_updateTask() {
        //prepare
        TaskEntity taskEntity = buildTaskEntity();
        taskRepository.save(taskEntity);
        String taskId = taskEntity.getId();
        TaskUpdateApiRequest request = buildUpdateRequest()
                .status("INVALID_STATUS")
                .build();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.exchange(TASK_URL + "/" + taskId, HttpMethod.PUT, new HttpEntity<>(request), ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(TASK_STATUS_INVALID);
    }

    @Test
    void whenStatusNull_updateTask() {
        //prepare
        TaskEntity taskEntity = buildTaskEntity();
        taskRepository.save(taskEntity);
        String taskId = taskEntity.getId();
        TaskUpdateApiRequest request = buildUpdateRequest()
                .status(null)
                .build();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.exchange(TaskController.TASK_URL + "/" + taskId, HttpMethod.PUT, new HttpEntity<>(request), ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(TASK_STATUS_NULL);
    }

    @Test
    void whenStatusEmpty_updateTask() {
        //prepare
        TaskEntity taskEntity = buildTaskEntity();
        taskRepository.save(taskEntity);
        String taskId = taskEntity.getId();
        TaskUpdateApiRequest request = buildUpdateRequest()
                .status("")
                .build();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.exchange(TaskController.TASK_URL + "/" + taskId, HttpMethod.PUT, new HttpEntity<>(request), ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(TASK_STATUS_NULL);
    }

    @Test
    void updateTaskWhenTAskNotExist() {
        //prepare
        String taskId = UUID.randomUUID().toString();
        TaskUpdateApiRequest request = buildUpdateRequest().build();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.exchange(TaskController.TASK_URL + "/" + taskId, HttpMethod.PUT, new HttpEntity<>(request), ErrorResponse.class);
        ;
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(String.format(TASK_NOT_EXIST, taskId));
    }

    @Test
    void whenExecutorNotExist_updateTask() {
        //prepare
        TaskEntity taskEntity = buildTaskEntity();
        taskRepository.save(taskEntity);
        String taskId = taskEntity.getId();
        Integer executorId = getUserId() + 1;
        TaskUpdateApiRequest request = buildUpdateRequest()
                .executorIds(Set.of(executorId))
                .status("")
                .build();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.exchange(TaskController.TASK_URL + "/" + taskId, HttpMethod.PUT, new HttpEntity<>(request), ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(TASK_STATUS_NULL);
    }

    private TaskCreateApiRequest.TaskCreateApiRequestBuilder buildCreateRequest() {
        return TaskCreateApiRequest.builder()
                .name("Name of task")
                .description("Description of task")
                .priority(Priority.HIGH.name())
                .reporterId(getUserId())
                .executorIds(Set.of())
                .confidential(true)
                .deadline(LocalDate.now().plusDays(1));
    }

    private Integer getUserId() {
        UserEntity userEntity = new UserEntity();
        userEntity.setName("user name");
        userEntity.setAdmin(false);
        userEntity.setCreatedAt(LocalDateTime.now());
        userEntity.setUpdatedAt(LocalDateTime.now());
        userRepository.save(userEntity);
        return userEntity.getId();
    }

    private TaskUpdateApiRequest.TaskUpdateApiRequestBuilder buildUpdateRequest() {
        return TaskUpdateApiRequest.builder()
                .name("Name after update")
                .description("Description after update")
                .confidential(true)
                .deadline(LocalDate.now().plusDays(1))
                .executorIds(Set.of())
                .priority(Priority.LOW.name())
                .status(TaskStatus.CANCELLED.name());
    }

    private TaskEntity buildTaskEntity() {

        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(UUID.randomUUID().toString());
        taskEntity.setName("Name of task");
        taskEntity.setDescription("Description of task");
        taskEntity.setPriority((Priority.HIGH));
        taskEntity.setReporterId(getUserId());
        taskEntity.setExecutorIds(Set.of());
        taskEntity.setConfidential(false);
        taskEntity.setDeadline(LocalDate.now().plusDays(1));
        taskEntity.setCreatedAt(LocalDateTime.now());
        taskEntity.setUpdatedAt(LocalDateTime.now());
        taskEntity.setStatus(TaskStatus.TO_DO);

        return taskEntity;
    }
}