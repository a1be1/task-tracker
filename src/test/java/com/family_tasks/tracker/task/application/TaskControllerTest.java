package com.family_tasks.tracker.task.application;

import com.family_tasks.tracker.AbstractApplicationTest;
import com.family_tasks.tracker.common.error.ErrorResponse;
import com.family_tasks.tracker.task.infrastructure.TaskRepository;
import com.family_tasks.tracker.task.model.dto.CreateTaskApiRequest;
import com.family_tasks.tracker.task.model.dto.CreateTaskApiRequest.CreateTaskApiRequestBuilder;
import com.family_tasks.tracker.task.model.dto.TaskApiResponse;
import com.family_tasks.tracker.task.model.dto.UpdateTaskApiRequest;
import com.family_tasks.tracker.task.model.dto.UpdateTaskApiRequest.UpdateTaskApiRequestBuilder;
import com.family_tasks.tracker.task.model.entity.TaskEntity;
import com.family_tasks.tracker.task.model.enums.Priority;
import com.family_tasks.tracker.task.model.enums.TaskStatus;
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
public class TaskControllerTest extends AbstractApplicationTest {

    @Autowired
    TaskRepository taskRepository;

    @Test
    void createTask() {
        //prepare
        CreateTaskApiRequest request = buildCreateRequest().build();
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
        assertThat(response.getExecutorId()).isEqualTo(request.getExecutorId());
        assertThat(response.isConfidential()).isEqualTo(request.getConfidential());
        assertThat(response.getSharedWith()).isEqualTo(request.getSharedWith());
        assertThat(response.getDeadline()).isEqualTo(request.getDeadline());

        TaskEntity taskEntity = taskRepository.getTask(response.getTaskId());
        assertThat(taskEntity).isNotNull();
    }

    @Test
    void whenPriorityInvalid_createTask() {
        //prepare
        CreateTaskApiRequest request = buildCreateRequest()
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
        CreateTaskApiRequest request = buildCreateRequest()
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
        CreateTaskApiRequest request = buildCreateRequest()
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
        CreateTaskApiRequest request = buildCreateRequest()
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
        CreateTaskApiRequest request = buildCreateRequest()
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
        CreateTaskApiRequest request = buildCreateRequest()
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
        CreateTaskApiRequest request = buildCreateRequest()
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
        CreateTaskApiRequest request = buildCreateRequest()
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
        CreateTaskApiRequest request = buildCreateRequest()
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
        CreateTaskApiRequest request = buildCreateRequest()
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
    void updateTaskWhenTaskExist() {
        //prepare
        TaskEntity taskEntity = buildTaskEntity();
        taskRepository.saveTask(taskEntity);
        String taskId = taskEntity.getTaskId();
        UpdateTaskApiRequest request = buildUpdateRequest().build();
        //execute
        ResponseEntity<TaskApiResponse> responseEntity = client.exchange(TaskController.TASK_URL + "/" + taskId, HttpMethod.PUT, new HttpEntity<>(request), TaskApiResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        TaskApiResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(TaskStatus.TO_DO);
        assertThat(response.getName()).isEqualTo(request.getName());
        assertThat(response.getDescription()).isEqualTo(request.getDescription());
        assertThat(response.getPriority().name()).isEqualTo(request.getPriority());
        assertThat(response.getExecutorId()).isEqualTo(request.getExecutorId());
        assertThat(response.isConfidential()).isEqualTo(request.getConfidential());
        assertThat(response.getSharedWith()).isEqualTo(request.getSharedWith());
        assertThat(response.getDeadline()).isEqualTo(request.getDeadline());
    }

    @Test
    void whenPriorityInvalid_updateTask() {
        //prepare
        TaskEntity taskEntity = buildTaskEntity();
        taskRepository.saveTask(taskEntity);
        String taskId = taskEntity.getTaskId();
        UpdateTaskApiRequest request = buildUpdateRequest()
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
        taskRepository.saveTask(taskEntity);
        String taskId = taskEntity.getTaskId();
        UpdateTaskApiRequest request = buildUpdateRequest()
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
        taskRepository.saveTask(taskEntity);
        String taskId = taskEntity.getTaskId();
        UpdateTaskApiRequest request = buildUpdateRequest()
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
        taskRepository.saveTask(taskEntity);
        String taskId = taskEntity.getTaskId();
        UpdateTaskApiRequest request = buildUpdateRequest()
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
        taskRepository.saveTask(taskEntity);
        String taskId = taskEntity.getTaskId();
        UpdateTaskApiRequest request = buildUpdateRequest()
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
        taskRepository.saveTask(taskEntity);
        String taskId = taskEntity.getTaskId();
        UpdateTaskApiRequest request = buildUpdateRequest()
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
        taskRepository.saveTask(taskEntity);
        String taskId = taskEntity.getTaskId();
        UpdateTaskApiRequest request = buildUpdateRequest()
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
        taskRepository.saveTask(taskEntity);
        String taskId = taskEntity.getTaskId();
        UpdateTaskApiRequest request = buildUpdateRequest()
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
        taskRepository.saveTask(taskEntity);
        String taskId = taskEntity.getTaskId();
        UpdateTaskApiRequest request = buildUpdateRequest()
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
        taskRepository.saveTask(taskEntity);
        String taskId = taskEntity.getTaskId();
        UpdateTaskApiRequest request = buildUpdateRequest()
                .status("INVALID_STATUS")
                .build();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.exchange(TaskController.TASK_URL + "/" + taskId, HttpMethod.PUT, new HttpEntity<>(request), ErrorResponse.class);
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
        taskRepository.saveTask(taskEntity);
        String taskId = taskEntity.getTaskId();
        UpdateTaskApiRequest request = buildUpdateRequest()
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
        taskRepository.saveTask(taskEntity);
        String taskId = taskEntity.getTaskId();
        UpdateTaskApiRequest request = buildUpdateRequest()
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
        UpdateTaskApiRequest request = buildUpdateRequest().build();
        //execute
        var responseEntity = client.exchange(TaskController.TASK_URL + "/" + taskId, HttpMethod.PUT, new HttpEntity<>(request), String.class);
        //validate
        //TODO write this test "Update not existing task"
    }

    private CreateTaskApiRequestBuilder buildCreateRequest() {
        return CreateTaskApiRequest.builder()
                .name("Name of task")
                .description("Description of task")
                .priority(Priority.HIGH.name())
                .reporterId(UUID.randomUUID().toString())
                .executorId(UUID.randomUUID().toString())
                .confidential(true)
                .sharedWith(Set.of(UUID.randomUUID().toString(), UUID.randomUUID().toString()))
                .deadline(LocalDate.now().plusDays(1));
    }

    private UpdateTaskApiRequestBuilder buildUpdateRequest() {
        return UpdateTaskApiRequest.builder()
                .name("Name after update")
                .description("Description after update")
                .confidential(true)
                .deadline(LocalDate.now().plusDays(1))
                .executorId(UUID.randomUUID().toString())
                .priority(Priority.LOW.name())
                .sharedWith(Set.of(UUID.randomUUID().toString(), UUID.randomUUID().toString()))
                .status(TaskStatus.CANCELLED.name());
    }

    private TaskEntity buildTaskEntity() {
        return TaskEntity.builder()
                .name("Name of task")
                .description("Description of task")
                .priority(Priority.HIGH)
                .reporterId(UUID.randomUUID().toString())
                .executorId(UUID.randomUUID().toString())
                .confidential(false)
                .sharedWith(Set.of(UUID.randomUUID().toString(), UUID.randomUUID().toString()))
                .deadline(LocalDate.now().plusDays(1))
                .taskId(UUID.randomUUID().toString())
                .status(TaskStatus.TO_DO)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}