package com.family_tasks.tracker.task.application;

import com.family_tasks.tracker.AbstractIntegrationTest;
import com.family_tasks.tracker.common.error.ErrorResponse;
import com.family_tasks.tracker.common.utils.TimeUtils;
import com.family_tasks.tracker.group.infrastructure.GroupRepository;
import com.family_tasks.tracker.group.model.entity.GroupEntity;
import com.family_tasks.tracker.task.infrastructure.TaskRepository;
import com.family_tasks.tracker.task.model.dto.TaskApiResponse;
import com.family_tasks.tracker.task.model.entity.TaskEntity;
import com.family_tasks.tracker.task.model.enums.TaskFilter;
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
    @Autowired
    GroupRepository groupRepository;

    @AfterEach
    void tearDown() {
        taskRepository.deleteAll();
    }

    @Test
    void whenTaskExist_getTask() {
        //prepare
        UserEntity user = createUser();
        Integer groupId = createGroup(user.getId());
        user.setGroupId(groupId);
        userRepository.save(user);

        TaskEntity taskEntity = buildTaskEntity(user.getId());
        taskRepository.save(taskEntity);
        String taskId = taskEntity.getId();
        String url = UriComponentsBuilder
                .fromUriString(TaskController.TASK_URL + "/" + taskId)
                .queryParam("userId", user.getId())
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
        UserEntity user = createUser();
        Integer groupId = createGroup(user.getId());
        user.setGroupId(groupId);
        userRepository.save(user);

        TaskEntity taskEntity = buildTaskEntity(user.getId());
        taskEntity.setConfidential(true);
        taskRepository.save(taskEntity);
        String taskId = taskEntity.getId();
        String url = UriComponentsBuilder
                .fromUriString(TaskController.TASK_URL + "/" + taskId)
                .queryParam("userId", user.getId())
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
        UserEntity user = createUser();
        Integer groupId = createGroup(user.getId());
        user.setGroupId(groupId);
        userRepository.save(user);

        TaskEntity taskEntity = buildTaskEntity(user.getId());
        taskEntity.setConfidential(true);
        UserEntity executorId = createUser();
        executorId.setGroupId(groupId);
        userRepository.save(executorId);

        taskEntity.setExecutorIds(Set.of(executorId.getId()));
        taskRepository.save(taskEntity);
        String taskId = taskEntity.getId();
        String url = UriComponentsBuilder
                .fromUriString(TaskController.TASK_URL + "/" + taskId)
                .queryParam("userId", user.getId())
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
        UserEntity user = createUser();
        Integer groupId = createGroup(user.getId());
        user.setGroupId(groupId);
        userRepository.save(user);

        TaskEntity taskEntity = buildTaskEntity(user.getId());
        taskEntity.setConfidential(true);

        UserEntity userIdWithoutPermission = createUser();
        userIdWithoutPermission.setGroupId(groupId);
        userRepository.save(userIdWithoutPermission);


        taskRepository.save(taskEntity);
        String taskId = taskEntity.getId();
        String url = UriComponentsBuilder
                .fromUriString(TaskController.TASK_URL + "/" + taskId)
                .queryParam("userId", userIdWithoutPermission.getId())
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
        UserEntity user = createUser();
        Integer groupId = createGroup(user.getId());
        user.setGroupId(groupId);
        userRepository.save(user);

        TaskEntity taskEntity = buildTaskEntity(user.getId());
        taskRepository.save(taskEntity);
        String taskId = taskEntity.getId();
        Integer notExistingUserId = createUser().getId() + 2;
        String url = UriComponentsBuilder
                .fromUriString(TaskController.TASK_URL + "/" + taskId)
                .queryParam("userId", notExistingUserId)
                .toUriString();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.getForEntity(url, ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(String.format(USER_NOT_EXIST, notExistingUserId));
    }

    @Test
    void whenUserNull_getTask() {
        //prepare
        UserEntity user = createUser();
        Integer groupId = createGroup(user.getId());
        user.setGroupId(groupId);
        userRepository.save(user);

        TaskEntity taskEntity = buildTaskEntity(user.getId());
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
        UserEntity user = createUser();
        Integer groupId = createGroup(user.getId());
        user.setGroupId(groupId);
        userRepository.save(user);

        TaskEntity taskEntity = buildTaskEntity(user.getId());
        taskRepository.save(taskEntity);
        String taskId = taskEntity.getId() + 1;
        String url = UriComponentsBuilder
                .fromUriString(TaskController.TASK_URL + "/" + taskId)
                .queryParam("userId", user.getId())
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
        UserEntity firstUser = createUser();
        Integer groupId = createGroup(firstUser.getId());
        firstUser.setGroupId(groupId);
        userRepository.save(firstUser);

        UserEntity secondUser = createUser();
        secondUser.setGroupId(groupId);
        userRepository.save(secondUser);

        TaskEntity taskEntityFirst = buildTaskEntity(firstUser.getId());
        TaskEntity taskEntitySecond = buildTaskEntity(secondUser.getId());
        taskRepository.save(taskEntityFirst);
        taskRepository.save(taskEntitySecond);
        String url = UriComponentsBuilder
                .fromUriString(TaskController.TASK_URL)
                .queryParam("userId", firstUser.getId())
                .queryParam("filter", TaskFilter.ALL_AVAILABLE.name())
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
    void whenUserFromAnotherGroup_getAllTask() {
        //prepare
        UserEntity firstUser = createUser();
        Integer firstGroupId = createGroup(firstUser.getId());
        firstUser.setGroupId(firstGroupId);
        userRepository.save(firstUser);

        UserEntity secondUser = createUser();
        Integer secondGroupId = createGroup(secondUser.getId());
        secondUser.setGroupId(secondGroupId);
        userRepository.save(secondUser);

        TaskEntity taskEntityFirst = buildTaskEntity(firstUser.getId());
        taskRepository.save(taskEntityFirst);
        String url = UriComponentsBuilder
                .fromUriString(TaskController.TASK_URL)
                .queryParam("userId", secondUser.getId())
                .queryParam("filter", TaskFilter.ALL_AVAILABLE.name())
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
        assertThat(response.isEmpty());
    }

    @Test
    void whenIsConfidentialTrueUndUserIsReporter_getAllTasks() {
        //prepare
        UserEntity user = createUser();
        Integer groupId = createGroup(user.getId());
        user.setGroupId(groupId);
        userRepository.save(user);

        TaskEntity taskEntity = buildTaskEntity(user.getId());
        taskEntity.setConfidential(true);
        taskRepository.save(taskEntity);
        String url = UriComponentsBuilder
                .fromUriString(TaskController.TASK_URL)
                .queryParam("userId", user.getId())
                .queryParam("filter", TaskFilter.ALL_AVAILABLE.name())
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
        UserEntity user = createUser();
        Integer groupId = createGroup(user.getId());
        user.setGroupId(groupId);
        userRepository.save(user);

        UserEntity executorId = createUser();
        executorId.setGroupId(groupId);
        userRepository.save(executorId);

        TaskEntity taskEntity = buildTaskEntity(user.getId());
        taskEntity.setConfidential(true);
        taskEntity.setExecutorIds(Set.of(executorId.getId()));
        taskRepository.save(taskEntity);
        String url = UriComponentsBuilder
                .fromUriString(TaskController.TASK_URL)
                .queryParam("userId", executorId.getId())
                .queryParam("filter", TaskFilter.ALL_AVAILABLE.name())
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
        UserEntity user = createUser();
        Integer groupId = createGroup(user.getId());
        user.setGroupId(groupId);
        userRepository.save(user);

        TaskEntity taskEntity = buildTaskEntity(user.getId());
        taskEntity.setConfidential(true);
        taskRepository.save(taskEntity);

        UserEntity userIdWithoutPermission = createUser();
        userIdWithoutPermission.setGroupId(groupId);
        userRepository.save(userIdWithoutPermission);

        String url = UriComponentsBuilder
                .fromUriString(TaskController.TASK_URL)
                .queryParam("userId", userIdWithoutPermission.getId())
                .queryParam("filter", TaskFilter.ALL_AVAILABLE.name())
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
        UserEntity user = createUser();
        Integer groupId = createGroup(user.getId());
        user.setGroupId(groupId);
        userRepository.save(user);

        TaskEntity taskEntity = buildTaskEntity(user.getId());
        taskEntity.setConfidential(true);
        taskEntity.setStatus("CANCELLED");
        taskRepository.save(taskEntity);

        UserEntity userIdWithoutPermission = createUser();
        userIdWithoutPermission.setGroupId(groupId);
        userRepository.save(userIdWithoutPermission);

        String url = UriComponentsBuilder
                .fromUriString(TaskController.TASK_URL)
                .queryParam("userId", userIdWithoutPermission.getId())
                .queryParam("filter", TaskFilter.ALL_CLOSED.name())
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
    void whenTaskIsCancelledAndUsersFromAnotherGroup_getAllTasks() {
        //prepare
        UserEntity firstUser = createUser();
        Integer firstGroupId = createGroup(firstUser.getId());
        firstUser.setGroupId(firstGroupId);
        userRepository.save(firstUser);

        UserEntity secondUser = createUser();
        Integer secondGroupId = createGroup(secondUser.getId());
        secondUser.setGroupId(secondGroupId);
        userRepository.save(secondUser);

        TaskEntity taskEntity = buildTaskEntity(firstUser.getId());
        taskEntity.setConfidential(true);
        taskEntity.setStatus("CANCELLED");
        taskRepository.save(taskEntity);

        String url = UriComponentsBuilder
                .fromUriString(TaskController.TASK_URL)
                .queryParam("userId", secondUser.getId())
                .queryParam("filter", TaskFilter.ALL_CLOSED.name())
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
        assertThat(response.isEmpty());
    }

    @Test
    void whenUserIsReporterGetActiveTaskAndTaskStatusToDo_getAllTasks() {
        //prepare
        UserEntity user = createUser();
        Integer groupId = createGroup(user.getId());
        user.setGroupId(groupId);
        userRepository.save(user);

        TaskEntity taskEntity = buildTaskEntity(user.getId());
        taskEntity.setConfidential(true);
        taskEntity.setStatus("TO_DO");
        taskRepository.save(taskEntity);
        String url = UriComponentsBuilder
                .fromUriString(TaskController.TASK_URL)
                .queryParam("userId", user.getId())
                .queryParam("filter", TaskFilter.IS_REPORTER_ACTIVE_TASK.name())
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
        UserEntity user = createUser();
        Integer groupId = createGroup(user.getId());
        user.setGroupId(groupId);
        userRepository.save(user);

        TaskEntity taskEntity = buildTaskEntity(user.getId());
        taskEntity.setConfidential(true);
        taskEntity.setStatus("IN_PROGRESS");
        taskRepository.save(taskEntity);
        String url = UriComponentsBuilder
                .fromUriString(TaskController.TASK_URL)
                .queryParam("userId", user.getId())
                .queryParam("filter", TaskFilter.IS_REPORTER_ACTIVE_TASK.name())
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
        UserEntity user = createUser();
        Integer groupId = createGroup(user.getId());
        user.setGroupId(groupId);
        userRepository.save(user);

        TaskEntity taskEntity = buildTaskEntity(user.getId());
        taskEntity.setConfidential(true);
        taskEntity.setStatus("COMPLETED");
        taskRepository.save(taskEntity);
        String url = UriComponentsBuilder
                .fromUriString(TaskController.TASK_URL)
                .queryParam("userId", user.getId())
                .queryParam("filter", TaskFilter.IS_REPORTER_COMPLETED_TASK.name())
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
        UserEntity user = createUser();
        Integer groupId = createGroup(user.getId());
        user.setGroupId(groupId);
        userRepository.save(user);

        TaskEntity taskEntity = buildTaskEntity(user.getId());
        taskEntity.setConfidential(true);
        taskEntity.setStatus("TO_DO");

        UserEntity executorId = createUser();
        taskEntity.setExecutorIds(Set.of(executorId.getId()));
        taskRepository.save(taskEntity);
        String url = UriComponentsBuilder
                .fromUriString(TaskController.TASK_URL)
                .queryParam("userId", executorId.getId())
                .queryParam("filter", TaskFilter.IS_EXECUTOR_ACTIVE_TASK.name())
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
        UserEntity user = createUser();
        Integer groupId = createGroup(user.getId());
        user.setGroupId(groupId);
        userRepository.save(user);

        TaskEntity taskEntity = buildTaskEntity(user.getId());
        taskEntity.setConfidential(true);
        taskEntity.setStatus("IN_PROGRESS");
        UserEntity executorId = createUser();
        taskEntity.setExecutorIds(Set.of(executorId.getId()));
        taskRepository.save(taskEntity);
        String url = UriComponentsBuilder
                .fromUriString(TaskController.TASK_URL)
                .queryParam("userId", executorId.getId())
                .queryParam("filter", TaskFilter.IS_EXECUTOR_ACTIVE_TASK.name())
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
        UserEntity user = createUser();
        Integer groupId = createGroup(user.getId());
        user.setGroupId(groupId);
        userRepository.save(user);

        TaskEntity taskEntity = buildTaskEntity(user.getId());
        taskEntity.setConfidential(true);
        taskEntity.setStatus("COMPLETED");
        UserEntity executorId = createUser();
        taskEntity.setExecutorIds(Set.of(executorId.getId()));
        taskRepository.save(taskEntity);
        String url = UriComponentsBuilder
                .fromUriString(TaskController.TASK_URL)
                .queryParam("userId", executorId.getId())
                .queryParam("filter", TaskFilter.IS_EXECUTOR_COMPLETED_TASK.name())
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
        UserEntity user = createUser();
        Integer groupId = createGroup(user.getId());
        user.setGroupId(groupId);
        userRepository.save(user);

        TaskEntity taskEntity = buildTaskEntity(createUser().getId());
        Integer notExistUserId = createUser().getId() + 2;
        taskRepository.save(taskEntity);
        String url = UriComponentsBuilder
                .fromUriString(TaskController.TASK_URL)
                .queryParam("userId", notExistUserId)
                .queryParam("filter", TaskFilter.IS_EXECUTOR_COMPLETED_TASK.name())
                .toUriString();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.getForEntity(url, ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(String.format(USER_NOT_EXIST, notExistUserId));
    }

    @Test
    void whenUserIsNull_getAllTasks() {
        UserEntity user = createUser();
        Integer groupId = createGroup(user.getId());
        user.setGroupId(groupId);
        userRepository.save(user);

        TaskEntity taskEntity = buildTaskEntity(user.getId());
        Integer notExistUserId = null;
        taskRepository.save(taskEntity);
        String url = UriComponentsBuilder
                .fromUriString(TaskController.TASK_URL)
                .queryParam("userId", notExistUserId)
                .queryParam("filter", TaskFilter.IS_EXECUTOR_COMPLETED_TASK.name())
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
        UserEntity user = createUser();
        Integer groupId = createGroup(user.getId());
        user.setGroupId(groupId);
        userRepository.save(user);

        TaskEntity taskEntity = buildTaskEntity(user.getId());
        taskRepository.save(taskEntity);
        String url = UriComponentsBuilder
                .fromUriString(TaskController.TASK_URL)
                .queryParam("userId", user.getId())
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

    private UserEntity createUser() {
        UserEntity userEntity = new UserEntity();
        userEntity.setName("user name");
        userEntity.setAdmin(false);
        userEntity.setCreatedAt(LocalDateTime.now());
        userEntity.setUpdatedAt(LocalDateTime.now());
        userRepository.save(userEntity);
        return userEntity;
    }

    private TaskEntity buildTaskEntity(Integer userId) {

        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(UUID.randomUUID().toString());
        taskEntity.setName("Name of task");
        taskEntity.setDescription("Description of task");
        taskEntity.setPriority((TaskPriority.HIGH.name()));
        taskEntity.setReporterId(userId);
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