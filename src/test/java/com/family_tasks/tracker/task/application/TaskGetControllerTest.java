package com.family_tasks.tracker.task.application;

import com.family_tasks.tracker.AbstractIntegrationTest;
import com.family_tasks.tracker.common.error.ErrorResponse;
import com.family_tasks.tracker.common.utils.TimeUtils;
import com.family_tasks.tracker.task.infrastructure.TaskRepository;
import com.family_tasks.tracker.task.model.dto.TaskApiResponse;
import com.family_tasks.tracker.task.model.entity.TaskEntity;
import com.family_tasks.tracker.task.model.enums.TaskPriority;
import com.family_tasks.tracker.task.model.enums.TaskStatus;
import com.family_tasks.tracker.user.infrastructure.UserRepository;
import com.family_tasks.tracker.user.model.entity.UserEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.family_tasks.tracker.common.validation.ValidationMessage.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link TaskController}
 */
public class TaskGetControllerTest extends AbstractIntegrationTest {

    @Autowired
    TaskRepository taskRepository;
    @Autowired
    UserRepository userRepository;

    @AfterEach
    void tearDown() {
        taskRepository.deleteAll();
    }

    @Test
    void whenTaskExist_getTask() {
        //prepare
        TaskEntity taskEntity = buildTaskEntity();
        taskRepository.save(taskEntity);
        String taskId = taskEntity.getId();
        Integer userId = taskEntity.getReporterId();
        String url = UriComponentsBuilder
                .fromUriString(TaskController.TASK_URL + "/" + taskId)
                .queryParam("userId", userId)
                .toUriString();
        //execute
        ResponseEntity<TaskApiResponse> responseEntity = client.getForEntity(url, TaskApiResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        TaskApiResponse response = responseEntity.getBody();
        assertThat(response).isEqualTo(toApiResponse(taskEntity));
    }

    @Test
    void whenIsConfidentialTrueForReporter_getTask() {
        //prepare
        TaskEntity taskEntity = buildTaskEntity();
        taskEntity.setConfidential(true);
        taskRepository.save(taskEntity);
        String taskId = taskEntity.getId();
        Integer userId = taskEntity.getReporterId();
        String url = UriComponentsBuilder
                .fromUriString(TaskController.TASK_URL + "/" + taskId)
                .queryParam("userId", userId)
                .toUriString();
        //execute
        ResponseEntity<TaskApiResponse> responseEntity = client.getForEntity(url, TaskApiResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        TaskApiResponse response = responseEntity.getBody();
        assertThat(response).isEqualTo(toApiResponse(taskEntity));
    }

    @Test
    void whenIsConfidentialTrueForExecutor_getTask() {
        //prepare
        TaskEntity taskEntity = buildTaskEntity();
        taskEntity.setConfidential(true);
        Integer userId = createUser();
        taskEntity.setExecutorIds(Set.of(userId));
        taskRepository.save(taskEntity);
        String taskId = taskEntity.getId();
        String url = UriComponentsBuilder
                .fromUriString(TaskController.TASK_URL + "/" + taskId)
                .queryParam("userId", userId)
                .toUriString();
        //execute
        ResponseEntity<TaskApiResponse> responseEntity = client.getForEntity(url, TaskApiResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        TaskApiResponse response = responseEntity.getBody();
        assertThat(response).isEqualTo(toApiResponse(taskEntity));
    }

    @Test
    void whenIsConfidentialWithoutPermission_getTask() {
        //prepare
        TaskEntity taskEntity = buildTaskEntity();
        taskEntity.setConfidential(true);
        Integer userId = createUser();
        taskRepository.save(taskEntity);
        String taskId = taskEntity.getId();
        String url = UriComponentsBuilder
                .fromUriString(TaskController.TASK_URL + "/" + taskId)
                .queryParam("userId", userId)
                .toUriString();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.getForEntity(url, ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(String.format(TASK_NOT_EXIST, taskId));
    }

    @Test
    void whenUserNotExist_getTask() {
        //prepare
        TaskEntity taskEntity = buildTaskEntity();
        taskRepository.save(taskEntity);
        String taskId = taskEntity.getId();
        Integer userId = createUser() + 2;
        String url = UriComponentsBuilder
                .fromUriString(TaskController.TASK_URL + "/" + taskId)
                .queryParam("userId", userId)
                .toUriString();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.getForEntity(url, ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(String.format(USER_NOT_EXIST, userId));
    }

    @Test
    void whenUserNull_getTask() {
        //prepare
        TaskEntity taskEntity = buildTaskEntity();
        taskRepository.save(taskEntity);
        String taskId = taskEntity.getId();
        String url = UriComponentsBuilder
                .fromUriString(TaskController.TASK_URL + "/" + taskId)
                .queryParam("userId", "")
                .toUriString();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.getForEntity(url, ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(String.format(USER_NOT_SPECIFIED));
    }

    @Test
    void whenTaskNotExist_getTask() {
        //prepare
        TaskEntity taskEntity = buildTaskEntity();
        taskRepository.save(taskEntity);
        String taskId = taskEntity.getId() + 1;
        Integer userId = taskEntity.getReporterId();
        String url = UriComponentsBuilder
                .fromUriString(TaskController.TASK_URL + "/" + taskId)
                .queryParam("userId", userId)
                .toUriString();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.getForEntity(url, ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(String.format(TASK_NOT_EXIST, taskId));
    }

    @Test
    void whenIsConfidentialFalse_getAllTask() {
        //prepare
        TaskEntity taskEntityFirst = buildTaskEntity();
        TaskEntity taskEntitySecond = buildTaskEntity();
        taskRepository.save(taskEntityFirst);
        taskRepository.save(taskEntitySecond);
        Integer userId = taskEntityFirst.getReporterId();
        String url = UriComponentsBuilder
                .fromUriString(TaskController.TASK_URL + "/all")
                .queryParam("userId", userId)
                .queryParam("filter", "ALL_AVAILABLE")
                .toUriString();
        //execute
        ResponseEntity<List<TaskApiResponse>> responseEntity =
                client.exchange(
                        url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<TaskApiResponse>>() {
                        }
                );
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<TaskApiResponse> response = responseEntity.getBody();
        assert response != null;
        assertThat(response).containsExactlyInAnyOrderElementsOf(List.of(toApiResponse(taskEntityFirst), toApiResponse(taskEntitySecond)));
    }

    @Test
    void whenIsConfidentialTrueUndUserIsReporter_getAllTasks() {
        //prepare
        TaskEntity taskEntity = buildTaskEntity();
        taskEntity.setConfidential(true);
        taskRepository.save(taskEntity);
        Integer userId = taskEntity.getReporterId();
        String url = UriComponentsBuilder
                .fromUriString(TaskController.TASK_URL + "/all")
                .queryParam("userId", userId)
                .queryParam("filter", "ALL_AVAILABLE")
                .toUriString();
        //execute
        ResponseEntity<List<TaskApiResponse>> responseEntity =
                client.exchange(
                        url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<TaskApiResponse>>() {
                        }
                );
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<TaskApiResponse> response = responseEntity.getBody();
        assert response != null;
        assertThat(response).containsExactlyInAnyOrderElementsOf(List.of(toApiResponse(taskEntity)));
    }

    @Test
    void whenIsConfidentialTrueUndUserIsExecutor_getAllTasks() {
        //prepare
        TaskEntity taskEntity = buildTaskEntity();
        taskEntity.setConfidential(true);
        Integer userId = createUser();
        taskEntity.setExecutorIds(Set.of(userId));
        taskRepository.save(taskEntity);
        String url = UriComponentsBuilder
                .fromUriString(TaskController.TASK_URL + "/all")
                .queryParam("userId", userId)
                .queryParam("filter", "ALL_AVAILABLE")
                .toUriString();
        //execute
        ResponseEntity<List<TaskApiResponse>> responseEntity =
                client.exchange(
                        url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<TaskApiResponse>>() {
                        }
                );
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<TaskApiResponse> response = responseEntity.getBody();
        assert response != null;
        assertThat(response).containsExactlyInAnyOrderElementsOf(List.of(toApiResponse(taskEntity)));
    }

    @Test
    void whenIsConfidentialTrueUndUserDoesNotHavePermission_getAllTasks() {
        //prepare
        TaskEntity taskEntity = buildTaskEntity();
        taskEntity.setConfidential(true);
        taskRepository.save(taskEntity);
        Integer userId = createUser();
        String url = UriComponentsBuilder
                .fromUriString(TaskController.TASK_URL + "/all")
                .queryParam("userId", userId)
                .queryParam("filter", "ALL_AVAILABLE")
                .toUriString();
        //execute
        ResponseEntity<List<TaskApiResponse>> responseEntity =
                client.exchange(
                        url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<TaskApiResponse>>() {
                        }
                );
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<TaskApiResponse> response = responseEntity.getBody();
        assert response != null;
        assertThat(response).doesNotContainAnyElementsOf(List.of(toApiResponse(taskEntity)));
    }

    @Test
    void whenTaskIsCancelled_getAllTasks() {
        //prepare
        TaskEntity taskEntity = buildTaskEntity();
        taskEntity.setConfidential(true);
        taskEntity.setStatus("CANCELLED");
        taskRepository.save(taskEntity);
        Integer userId = createUser();
        String url = UriComponentsBuilder
                .fromUriString(TaskController.TASK_URL + "/all")
                .queryParam("userId", userId)
                .queryParam("filter", "ALL_CANCELLED")
                .toUriString();
        //execute
        ResponseEntity<List<TaskApiResponse>> responseEntity =
                client.exchange(
                        url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<TaskApiResponse>>() {
                        }
                );
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<TaskApiResponse> response = responseEntity.getBody();
        assert response != null;
        assertThat(response).containsExactlyInAnyOrderElementsOf(List.of(toApiResponse(taskEntity)));
    }

    @Test
    void whenUserIsReporterGetActiveTaskAndTaskStatusToDo_getAllTasks() {
        //prepare
        TaskEntity taskEntity = buildTaskEntity();
        taskEntity.setConfidential(true);
        taskEntity.setStatus("TO_DO");
        taskRepository.save(taskEntity);
        Integer userId = taskEntity.getReporterId();
        String url = UriComponentsBuilder
                .fromUriString(TaskController.TASK_URL + "/all")
                .queryParam("userId", userId)
                .queryParam("filter", "IS_REPORTER_ACTIVE_TASK")
                .toUriString();
        //execute
        ResponseEntity<List<TaskApiResponse>> responseEntity =
                client.exchange(
                        url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<TaskApiResponse>>() {
                        }
                );
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<TaskApiResponse> response = responseEntity.getBody();
        assert response != null;
        assertThat(response).containsExactlyInAnyOrderElementsOf(List.of(toApiResponse(taskEntity)));
    }

    @Test
    void whenUserIsReporterGetActiveTaskAndTaskStatusInProgress_getAllTasks() {
        //prepare
        TaskEntity taskEntity = buildTaskEntity();
        taskEntity.setConfidential(true);
        taskEntity.setStatus("IN_PROGRESS");
        taskRepository.save(taskEntity);
        Integer userId = taskEntity.getReporterId();
        String url = UriComponentsBuilder
                .fromUriString(TaskController.TASK_URL + "/all")
                .queryParam("userId", userId)
                .queryParam("filter", "IS_REPORTER_ACTIVE_TASK")
                .toUriString();
        //execute
        ResponseEntity<List<TaskApiResponse>> responseEntity =
                client.exchange(
                        url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<TaskApiResponse>>() {
                        }
                );
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<TaskApiResponse> response = responseEntity.getBody();
        assert response != null;
        assertThat(response).containsExactlyInAnyOrderElementsOf(List.of(toApiResponse(taskEntity)));
    }

    @Test
    void whenUserIsReporterGetACompletedTask_getAllTasks() {
        //prepare
        TaskEntity taskEntity = buildTaskEntity();
        taskEntity.setConfidential(true);
        taskEntity.setStatus("COMPLETED");
        taskRepository.save(taskEntity);
        Integer userId = taskEntity.getReporterId();
        String url = UriComponentsBuilder
                .fromUriString(TaskController.TASK_URL + "/all")
                .queryParam("userId", userId)
                .queryParam("filter", "IS_REPORTER_COMPLETED_TASK")
                .toUriString();
        //execute
        ResponseEntity<List<TaskApiResponse>> responseEntity =
                client.exchange(
                        url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<TaskApiResponse>>() {
                        }
                );

        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<TaskApiResponse> response = responseEntity.getBody();
        assert response != null;
        assertThat(response).containsExactlyInAnyOrderElementsOf(List.of(toApiResponse(taskEntity)));
    }

    @Test
    void whenUserIsExecutorGetActiveTaskAndTaskStatusToDo_getAllTasks() {
        //prepare
        TaskEntity taskEntity = buildTaskEntity();
        taskEntity.setConfidential(true);
        taskEntity.setStatus("TO_DO");
        Integer userId = createUser();
        taskEntity.setExecutorIds(Set.of(userId));
        taskRepository.save(taskEntity);
        String url = UriComponentsBuilder
                .fromUriString(TaskController.TASK_URL + "/all")
                .queryParam("userId", userId)
                .queryParam("filter", "IS_EXECUTOR_ACTIVE_TASK")
                .toUriString();
        //execute
        ResponseEntity<List<TaskApiResponse>> responseEntity =
                client.exchange(
                        url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<TaskApiResponse>>() {
                        }
                );
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<TaskApiResponse> response = responseEntity.getBody();
        assert response != null;
        assertThat(response).containsExactlyInAnyOrderElementsOf(List.of(toApiResponse(taskEntity)));
    }

    @Test
    void whenUserIsExecutorGetActiveTaskAndTaskStatusInProgress_getAllTasks() {
        //prepare
        TaskEntity taskEntity = buildTaskEntity();
        taskEntity.setConfidential(true);
        taskEntity.setStatus("IN_PROGRESS");
        Integer userId = createUser();
        taskEntity.setExecutorIds(Set.of(userId));
        taskRepository.save(taskEntity);
        String url = UriComponentsBuilder
                .fromUriString(TaskController.TASK_URL + "/all")
                .queryParam("userId", userId)
                .queryParam("filter", "IS_EXECUTOR_ACTIVE_TASK")
                .toUriString();
        //execute
        ResponseEntity<List<TaskApiResponse>> responseEntity =
                client.exchange(
                        url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<TaskApiResponse>>() {
                        }
                );
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<TaskApiResponse> response = responseEntity.getBody();
        assert response != null;
        assertThat(response).containsExactlyInAnyOrderElementsOf(List.of(toApiResponse(taskEntity)));
    }

    @Test
    void whenUserIsExecutorGetACompletedTask_getAllTasks() {
        //prepare
        TaskEntity taskEntity = buildTaskEntity();
        taskEntity.setConfidential(true);
        taskEntity.setStatus("COMPLETED");
        Integer userId = createUser();
        taskEntity.setExecutorIds(Set.of(userId));
        taskRepository.save(taskEntity);
        String url = UriComponentsBuilder
                .fromUriString(TaskController.TASK_URL + "/all")
                .queryParam("userId", userId)
                .queryParam("filter", "IS_EXECUTOR_COMPLETED_TASK")
                .toUriString();
        //execute
        ResponseEntity<List<TaskApiResponse>> responseEntity =
                client.exchange(
                        url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<TaskApiResponse>>() {
                        }
                );
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<TaskApiResponse> response = responseEntity.getBody();
        assert response != null;
        assertThat(response).containsExactlyInAnyOrderElementsOf(List.of(toApiResponse(taskEntity)));
    }

    @Test
    void whenUserDoesNotExist_getAllTasks() {
        TaskEntity taskEntity = buildTaskEntity();
        Integer userId = createUser() + 2;
        taskRepository.save(taskEntity);
        String url = UriComponentsBuilder
                .fromUriString(TaskController.TASK_URL + "/all")
                .queryParam("userId", userId)
                .queryParam("filter", "IS_EXECUTOR_COMPLETED_TASK")
                .toUriString();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.getForEntity(url, ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(String.format(USER_NOT_EXIST, userId));
    }

    @Test
    void whenUserIsNull_getAllTasks() {
        TaskEntity taskEntity = buildTaskEntity();
        Integer userId = null;
        taskRepository.save(taskEntity);
        String url = UriComponentsBuilder
                .fromUriString(TaskController.TASK_URL + "/all")
                .queryParam("userId", userId)
                .queryParam("filter", "IS_EXECUTOR_COMPLETED_TASK")
                .toUriString();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.getForEntity(url, ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(String.format(USER_NOT_SPECIFIED));
    }

    @Test
    void whenTaskFilterDoesNotExist_getAllTasks() {
        TaskEntity taskEntity = buildTaskEntity();
        Integer userId = taskEntity.getReporterId();
        taskRepository.save(taskEntity);
        String url = UriComponentsBuilder
                .fromUriString(TaskController.TASK_URL + "/all")
                .queryParam("userId", userId)
                .queryParam("filter", "FilterNotExist")
                .toUriString();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.getForEntity(url, ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(TASK_FILTER_INVALID);
    }

    private Integer createUser() {
        UserEntity userEntity = new UserEntity();
        userEntity.setName("user name");
        userEntity.setAdmin(false);
        userEntity.setCreatedAt(LocalDateTime.now());
        userEntity.setUpdatedAt(LocalDateTime.now());
        userRepository.save(userEntity);
        return userEntity.getId();
    }

    private TaskEntity buildTaskEntity() {

        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(UUID.randomUUID().toString());
        taskEntity.setName("Name of task");
        taskEntity.setDescription("Description of task");
        taskEntity.setPriority((TaskPriority.HIGH.name()));
        taskEntity.setReporterId(createUser());
        taskEntity.setExecutorIds(Set.of());
        taskEntity.setConfidential(false);
        taskEntity.setDeadline(LocalDate.from(TimeUtils.now().plusDays(1)));
        taskEntity.setCreatedAt(TimeUtils.now());
        taskEntity.setUpdatedAt(TimeUtils.now());
        taskEntity.setStatus(TaskStatus.TO_DO.name());

        return taskEntity;
    }

    private TaskApiResponse toApiResponse(TaskEntity taskEntity) {
        return TaskApiResponse.builder()
                .taskId(taskEntity.getId())
                .status(TaskStatus.valueOf(taskEntity.getStatus()))
                .name(taskEntity.getName())
                .description(taskEntity.getDescription())
                .priority(TaskPriority.valueOf(taskEntity.getPriority()))
                .reporterId(taskEntity.getReporterId())
                .executorIds(taskEntity.getExecutorIds())
                .confidential(taskEntity.isConfidential())
                .deadline(taskEntity.getDeadline())
                .createdAt(taskEntity.getCreatedAt())
                .updatedAt(taskEntity.getUpdatedAt())
                .build();
    }
}