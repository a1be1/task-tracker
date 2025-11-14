package com.family_tasks.tracker.task.application;

import com.family_tasks.tracker.AbstractIntegrationTest;
import com.family_tasks.tracker.common.error.ErrorResponse;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Set;

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
        UserEntity user = createUserEntity();
        Integer groupId = createGroupEntity(user.getId());
        user.setGroupId(groupId);
        userRepository.save(user);

        TaskEntity taskEntity = createTaskEntity(user.getId());
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
        UserEntity user = createUserEntity();
        Integer groupId = createGroupEntity(user.getId());
        user.setGroupId(groupId);
        userRepository.save(user);

        TaskEntity taskEntity = createTaskEntity(user.getId());
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
        UserEntity user = createUserEntity();
        Integer groupId = createGroupEntity(user.getId());
        user.setGroupId(groupId);
        userRepository.save(user);

        TaskEntity taskEntity = createTaskEntity(user.getId());
        taskEntity.setConfidential(true);
        UserEntity executorId = createUserEntity();
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
        UserEntity user = createUserEntity();
        Integer groupId = createGroupEntity(user.getId());
        user.setGroupId(groupId);
        userRepository.save(user);

        TaskEntity taskEntity = createTaskEntity(user.getId());
        taskEntity.setConfidential(true);

        UserEntity userIdWithoutPermission = createUserEntity();
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
        UserEntity user = createUserEntity();
        Integer groupId = createGroupEntity(user.getId());
        user.setGroupId(groupId);
        userRepository.save(user);

        TaskEntity taskEntity = createTaskEntity(user.getId());
        taskRepository.save(taskEntity);
        String taskId = taskEntity.getId();
        Integer notExistingUserId = createUserEntity().getId() + 2;
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
        UserEntity user = createUserEntity();
        Integer groupId = createGroupEntity(user.getId());
        user.setGroupId(groupId);
        userRepository.save(user);

        TaskEntity taskEntity = createTaskEntity(user.getId());
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
        UserEntity user = createUserEntity();
        Integer groupId = createGroupEntity(user.getId());
        user.setGroupId(groupId);
        userRepository.save(user);

        TaskEntity taskEntity = createTaskEntity(user.getId());
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