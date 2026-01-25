package com.family_tasks.tracker.task.application;

import com.family_tasks.tracker.AbstractIntegrationTest;
import com.family_tasks.tracker.common.error.ErrorResponse;
import com.family_tasks.tracker.task.infrastructure.TaskRepository;
import com.family_tasks.tracker.task.model.dto.TaskApiResponse;
import com.family_tasks.tracker.task.model.entity.TaskEntity;
import com.family_tasks.tracker.task.model.enums.TaskFilter;
import com.family_tasks.tracker.task.model.enums.TaskPriority;
import com.family_tasks.tracker.task.model.enums.TaskStatus;
import com.family_tasks.tracker.user.infrastructure.UserRepository;
import com.family_tasks.tracker.user.model.entity.UserEntity;
import com.family_tasks.tracker.utils.SliceWrapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Set;

import static com.family_tasks.tracker.common.validation.ValidationMessage.*;
import static org.assertj.core.api.Assertions.assertThat;

public class TaskGetAllControllerTest extends AbstractIntegrationTest {
    @Autowired
    TaskRepository taskRepository;
    @Autowired
    UserRepository userRepository;

    @AfterEach
    void tearDown() {
        taskRepository.deleteAll();
    }

    @Test
    void whenIsConfidentialFalse_getAllTask() {
        //prepare
        UserEntity firstUser = createUserEntity();
        Integer groupId = createGroupEntity(firstUser.getId());
        firstUser.setGroupId(groupId);
        userRepository.save(firstUser);

        UserEntity secondUser = createUserEntity();
        secondUser.setGroupId(groupId);
        userRepository.save(secondUser);

        TaskEntity taskEntityFirst = createTaskEntity(firstUser.getId());
        TaskEntity taskEntitySecond = createTaskEntity(secondUser.getId());
        taskRepository.save(taskEntityFirst);
        taskRepository.save(taskEntitySecond);
        String url = UriComponentsBuilder
                .fromUriString(TaskController.TASK_URL)
                .queryParam("userId", firstUser.getId())
                .queryParam("filter", TaskFilter.ALL_AVAILABLE.name())
                .toUriString();
        // execute
        ResponseEntity<SliceWrapper<TaskApiResponse>> responseEntity =
                client.exchange(
                        url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<SliceWrapper<TaskApiResponse>>() {
                        }
                );
        // validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        SliceWrapper<TaskApiResponse> slice = responseEntity.getBody();
        assertThat(slice).isNotNull();
        assertThat(slice.getContent())
                .containsExactlyInAnyOrderElementsOf(
                        List.of(
                                toApiResponse(taskEntityFirst),
                                toApiResponse(taskEntitySecond)
                        )
                );
    }

    @Test
    void whenUserFromAnotherGroup_getAllTask() {
        //prepare
        UserEntity firstUser = createUserEntity();
        Integer firstGroupId = createGroupEntity(firstUser.getId());
        firstUser.setGroupId(firstGroupId);
        userRepository.save(firstUser);

        UserEntity secondUser = createUserEntity();
        Integer secondGroupId = createGroupEntity(secondUser.getId());
        secondUser.setGroupId(secondGroupId);
        userRepository.save(secondUser);

        TaskEntity taskEntityFirst = createTaskEntity(firstUser.getId());
        taskRepository.save(taskEntityFirst);
        String url = UriComponentsBuilder
                .fromUriString(TaskController.TASK_URL)
                .queryParam("userId", secondUser.getId())
                .queryParam("filter", TaskFilter.ALL_AVAILABLE.name())
                .toUriString();
        //execute
        ResponseEntity<SliceWrapper<TaskApiResponse>> responseEntity =
                client.exchange(
                        url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<SliceWrapper<TaskApiResponse>>() {
                        }
                );
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        SliceWrapper<TaskApiResponse> slice = responseEntity.getBody();
        assertThat(slice).isNotNull();
        assertThat(slice.getContent().isEmpty());
    }

    @Test
    void whenIsConfidentialTrueUndUserIsReporter_getAllTasks() {
        //prepare
        UserEntity user = createUserEntity();
        Integer groupId = createGroupEntity(user.getId());
        user.setGroupId(groupId);
        userRepository.save(user);

        TaskEntity taskEntity = createTaskEntity(user.getId());
        taskEntity.setConfidential(true);
        taskRepository.save(taskEntity);
        String url = UriComponentsBuilder
                .fromUriString(TaskController.TASK_URL)
                .queryParam("userId", user.getId())
                .queryParam("filter", TaskFilter.ALL_AVAILABLE.name())
                .toUriString();
        //execute
        ResponseEntity<SliceWrapper<TaskApiResponse>> responseEntity =
                client.exchange(
                        url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<SliceWrapper<TaskApiResponse>>() {
                        }
                );
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        SliceWrapper<TaskApiResponse> slice = responseEntity.getBody();
        assertThat(slice).isNotNull();
        assertThat(slice.getContent())
                .containsExactlyInAnyOrderElementsOf(List.of(toApiResponse(taskEntity)));
    }

    @Test
    void whenIsConfidentialTrueUndUserIsExecutor_getAllTasks() {
        //prepare
        UserEntity user = createUserEntity();
        Integer groupId = createGroupEntity(user.getId());
        user.setGroupId(groupId);
        userRepository.save(user);

        UserEntity executorId = createUserEntity();
        executorId.setGroupId(groupId);
        userRepository.save(executorId);

        TaskEntity taskEntity = createTaskEntity(user.getId());
        taskEntity.setConfidential(true);
        taskEntity.setExecutorIds(Set.of(executorId.getId()));
        taskRepository.save(taskEntity);
        String url = UriComponentsBuilder
                .fromUriString(TaskController.TASK_URL)
                .queryParam("userId", executorId.getId())
                .queryParam("filter", TaskFilter.ALL_AVAILABLE.name())
                .toUriString();
        //execute
        ResponseEntity<SliceWrapper<TaskApiResponse>> responseEntity =
                client.exchange(
                        url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<SliceWrapper<TaskApiResponse>>() {
                        }
                );
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        SliceWrapper<TaskApiResponse> slice = responseEntity.getBody();
        assertThat(slice).isNotNull();
        assertThat(slice.getContent())
                .containsExactlyInAnyOrderElementsOf(List.of(toApiResponse(taskEntity)));
    }

    @Test
    void whenIsConfidentialTrueUndUserDoesNotHavePermission_getAllTasks() {
        //prepare
        UserEntity user = createUserEntity();
        Integer groupId = createGroupEntity(user.getId());
        user.setGroupId(groupId);
        userRepository.save(user);

        TaskEntity taskEntity = createTaskEntity(user.getId());
        taskEntity.setConfidential(true);
        taskRepository.save(taskEntity);

        UserEntity userIdWithoutPermission = createUserEntity();
        userIdWithoutPermission.setGroupId(groupId);
        userRepository.save(userIdWithoutPermission);

        String url = UriComponentsBuilder
                .fromUriString(TaskController.TASK_URL)
                .queryParam("userId", userIdWithoutPermission.getId())
                .queryParam("filter", TaskFilter.ALL_AVAILABLE.name())
                .toUriString();
        //execute
        ResponseEntity<SliceWrapper<TaskApiResponse>> responseEntity =
                client.exchange(
                        url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<SliceWrapper<TaskApiResponse>>() {
                        }
                );
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        SliceWrapper<TaskApiResponse> slice = responseEntity.getBody();
        assertThat(slice).isNotNull();
        assertThat(slice.getContent().isEmpty());
    }

    @Test
    void whenTaskIsCancelled_getAllTasks() {
        //prepare
        UserEntity user = createUserEntity();
        Integer groupId = createGroupEntity(user.getId());
        user.setGroupId(groupId);
        userRepository.save(user);

        TaskEntity taskEntity = createTaskEntity(user.getId());
        taskEntity.setConfidential(true);
        taskEntity.setStatus("CANCELLED");
        taskRepository.save(taskEntity);

        UserEntity userIdWithoutPermission = createUserEntity();
        userIdWithoutPermission.setGroupId(groupId);
        userRepository.save(userIdWithoutPermission);

        String url = UriComponentsBuilder
                .fromUriString(TaskController.TASK_URL)
                .queryParam("userId", userIdWithoutPermission.getId())
                .queryParam("filter", TaskFilter.ALL_CLOSED.name())
                .toUriString();
        //execute
        ResponseEntity<SliceWrapper<TaskApiResponse>> responseEntity =
                client.exchange(
                        url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<SliceWrapper<TaskApiResponse>>() {
                        }
                );
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        SliceWrapper<TaskApiResponse> slice = responseEntity.getBody();
        assertThat(slice).isNotNull();
        assertThat(slice.getContent())
                .containsExactlyInAnyOrderElementsOf(List.of(toApiResponse(taskEntity)));
    }

    @Test
    void whenTaskIsCancelledAndUsersFromAnotherGroup_getAllTasks() {
        //prepare
        UserEntity firstUser = createUserEntity();
        Integer firstGroupId = createGroupEntity(firstUser.getId());
        firstUser.setGroupId(firstGroupId);
        userRepository.save(firstUser);

        UserEntity secondUser = createUserEntity();
        Integer secondGroupId = createGroupEntity(secondUser.getId());
        secondUser.setGroupId(secondGroupId);
        userRepository.save(secondUser);

        TaskEntity taskEntity = createTaskEntity(firstUser.getId());
        taskEntity.setConfidential(true);
        taskEntity.setStatus("CANCELLED");
        taskRepository.save(taskEntity);

        String url = UriComponentsBuilder
                .fromUriString(TaskController.TASK_URL)
                .queryParam("userId", secondUser.getId())
                .queryParam("filter", TaskFilter.ALL_CLOSED.name())
                .toUriString();
        //execute
        ResponseEntity<SliceWrapper<TaskApiResponse>> responseEntity =
                client.exchange(
                        url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<SliceWrapper<TaskApiResponse>>() {
                        }
                );
        //validate
        SliceWrapper<TaskApiResponse> slice = responseEntity.getBody();
        assertThat(slice).isNotNull();
        assertThat(slice.getContent().isEmpty());
    }

    @Test
    void whenUserIsReporterGetActiveTaskAndTaskStatusToDo_getAllTasks() {
        //prepare
        UserEntity user = createUserEntity();
        Integer groupId = createGroupEntity(user.getId());
        user.setGroupId(groupId);
        userRepository.save(user);

        TaskEntity taskEntity = createTaskEntity(user.getId());
        taskEntity.setConfidential(true);
        taskEntity.setStatus("TO_DO");
        taskRepository.save(taskEntity);
        String url = UriComponentsBuilder
                .fromUriString(TaskController.TASK_URL)
                .queryParam("userId", user.getId())
                .queryParam("filter", TaskFilter.IS_REPORTER_ACTIVE_TASK.name())
                .toUriString();
        //execute
        ResponseEntity<SliceWrapper<TaskApiResponse>> responseEntity =
                client.exchange(
                        url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<SliceWrapper<TaskApiResponse>>() {
                        }
                );
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        SliceWrapper<TaskApiResponse> slice = responseEntity.getBody();
        assertThat(slice).isNotNull();
        assertThat(slice.getContent())
                .containsExactlyInAnyOrderElementsOf(List.of(toApiResponse(taskEntity)));
    }

    @Test
    void whenUserIsReporterGetActiveTaskAndTaskStatusInProgress_getAllTasks() {
        //prepare
        UserEntity user = createUserEntity();
        Integer groupId = createGroupEntity(user.getId());
        user.setGroupId(groupId);
        userRepository.save(user);

        TaskEntity taskEntity = createTaskEntity(user.getId());
        taskEntity.setConfidential(true);
        taskEntity.setStatus("IN_PROGRESS");
        taskRepository.save(taskEntity);
        String url = UriComponentsBuilder
                .fromUriString(TaskController.TASK_URL)
                .queryParam("userId", user.getId())
                .queryParam("filter", TaskFilter.IS_REPORTER_ACTIVE_TASK.name())
                .toUriString();
        //execute
        ResponseEntity<SliceWrapper<TaskApiResponse>> responseEntity =
                client.exchange(
                        url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<SliceWrapper<TaskApiResponse>>() {
                        }
                );
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        SliceWrapper<TaskApiResponse> slice = responseEntity.getBody();
        assertThat(slice).isNotNull();
        assertThat(slice.getContent())
                .containsExactlyInAnyOrderElementsOf(List.of(toApiResponse(taskEntity)));
    }

    @Test
    void whenUserIsReporterGetACompletedTask_getAllTasks() {
        //prepare
        UserEntity user = createUserEntity();
        Integer groupId = createGroupEntity(user.getId());
        user.setGroupId(groupId);
        userRepository.save(user);

        TaskEntity taskEntity = createTaskEntity(user.getId());
        taskEntity.setConfidential(true);
        taskEntity.setStatus("COMPLETED");
        taskRepository.save(taskEntity);
        String url = UriComponentsBuilder
                .fromUriString(TaskController.TASK_URL)
                .queryParam("userId", user.getId())
                .queryParam("filter", TaskFilter.IS_REPORTER_COMPLETED_TASK.name())
                .toUriString();
        //execute
        ResponseEntity<SliceWrapper<TaskApiResponse>> responseEntity =
                client.exchange(
                        url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<SliceWrapper<TaskApiResponse>>() {
                        }
                );
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        SliceWrapper<TaskApiResponse> slice = responseEntity.getBody();
        assertThat(slice).isNotNull();
        assertThat(slice.getContent())
                .containsExactlyInAnyOrderElementsOf(List.of(toApiResponse(taskEntity)));
    }

    @Test
    void whenUserIsExecutorGetActiveTaskAndTaskStatusToDo_getAllTasks() {
        //prepare
        UserEntity user = createUserEntity();
        Integer groupId = createGroupEntity(user.getId());
        user.setGroupId(groupId);
        userRepository.save(user);

        TaskEntity taskEntity = createTaskEntity(user.getId());
        taskEntity.setConfidential(true);
        taskEntity.setStatus("TO_DO");

        UserEntity executorId = createUserEntity();
        taskEntity.setExecutorIds(Set.of(executorId.getId()));
        taskRepository.save(taskEntity);
        String url = UriComponentsBuilder
                .fromUriString(TaskController.TASK_URL)
                .queryParam("userId", executorId.getId())
                .queryParam("filter", TaskFilter.IS_EXECUTOR_ACTIVE_TASK.name())
                .toUriString();
        //execute
        ResponseEntity<SliceWrapper<TaskApiResponse>> responseEntity =
                client.exchange(
                        url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<SliceWrapper<TaskApiResponse>>() {
                        }
                );
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        SliceWrapper<TaskApiResponse> slice = responseEntity.getBody();
        assertThat(slice).isNotNull();
        assertThat(slice.getContent())
                .containsExactlyInAnyOrderElementsOf(List.of(toApiResponse(taskEntity)));
    }

    @Test
    void whenUserIsExecutorGetActiveTaskAndTaskStatusInProgress_getAllTasks() {
        //prepare
        UserEntity user = createUserEntity();
        Integer groupId = createGroupEntity(user.getId());
        user.setGroupId(groupId);
        userRepository.save(user);

        TaskEntity taskEntity = createTaskEntity(user.getId());
        taskEntity.setConfidential(true);
        taskEntity.setStatus("IN_PROGRESS");
        UserEntity executorId = createUserEntity();
        taskEntity.setExecutorIds(Set.of(executorId.getId()));
        taskRepository.save(taskEntity);
        String url = UriComponentsBuilder
                .fromUriString(TaskController.TASK_URL)
                .queryParam("userId", executorId.getId())
                .queryParam("filter", TaskFilter.IS_EXECUTOR_ACTIVE_TASK.name())
                .toUriString();
        //execute
        ResponseEntity<SliceWrapper<TaskApiResponse>> responseEntity =
                client.exchange(
                        url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<SliceWrapper<TaskApiResponse>>() {
                        }
                );
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        SliceWrapper<TaskApiResponse> slice = responseEntity.getBody();
        assertThat(slice).isNotNull();
        assertThat(slice.getContent())
                .containsExactlyInAnyOrderElementsOf(List.of(toApiResponse(taskEntity)));
    }

    @Test
    void whenUserIsExecutorGetACompletedTask_getAllTasks() {
        //prepare
        UserEntity user = createUserEntity();
        Integer groupId = createGroupEntity(user.getId());
        user.setGroupId(groupId);
        userRepository.save(user);

        TaskEntity taskEntity = createTaskEntity(user.getId());
        taskEntity.setConfidential(true);
        taskEntity.setStatus("COMPLETED");
        UserEntity executorId = createUserEntity();
        taskEntity.setExecutorIds(Set.of(executorId.getId()));
        taskRepository.save(taskEntity);
        String url = UriComponentsBuilder
                .fromUriString(TaskController.TASK_URL)
                .queryParam("userId", executorId.getId())
                .queryParam("filter", TaskFilter.IS_EXECUTOR_COMPLETED_TASK.name())
                .toUriString();
        //execute
        ResponseEntity<SliceWrapper<TaskApiResponse>> responseEntity =
                client.exchange(
                        url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<SliceWrapper<TaskApiResponse>>() {
                        }
                );
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        SliceWrapper<TaskApiResponse> slice = responseEntity.getBody();
        assertThat(slice).isNotNull();
        assertThat(slice.getContent())
                .containsExactlyInAnyOrderElementsOf(List.of(toApiResponse(taskEntity)));
    }

    @Test
    void whenUserDoesNotExist_getAllTasks() {
        UserEntity user = createUserEntity();
        Integer groupId = createGroupEntity(user.getId());
        user.setGroupId(groupId);
        userRepository.save(user);

        TaskEntity taskEntity = createTaskEntity(createUserEntity().getId());
        Integer notExistUserId = createUserEntity().getId() + 2;
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
        UserEntity user = createUserEntity();
        Integer groupId = createGroupEntity(user.getId());
        user.setGroupId(groupId);
        userRepository.save(user);

        TaskEntity taskEntity = createTaskEntity(user.getId());
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
        UserEntity user = createUserEntity();
        Integer groupId = createGroupEntity(user.getId());
        user.setGroupId(groupId);
        userRepository.save(user);

        TaskEntity taskEntity = createTaskEntity(user.getId());
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
                .rewardsPoints(taskEntity.getRewardsPoints())
                .build();
    }
}