package com.family_tasks.tracker.task.application;

import com.family_tasks.tracker.AbstractIntegrationTest;
import com.family_tasks.tracker.common.error.ErrorResponse;
import com.family_tasks.tracker.group.infrastructure.GroupRepository;
import com.family_tasks.tracker.group.model.entity.GroupEntity;
import com.family_tasks.tracker.task.infrastructure.TaskRepository;
import com.family_tasks.tracker.task.model.dto.TaskApiResponse;
import com.family_tasks.tracker.task.model.dto.TaskUpdateApiRequest;
import com.family_tasks.tracker.task.model.entity.TaskEntity;
import com.family_tasks.tracker.task.model.enums.TaskPriority;
import com.family_tasks.tracker.task.model.enums.TaskStatus;
import com.family_tasks.tracker.user.infrastructure.UserRepository;
import com.family_tasks.tracker.user.model.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
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
import static com.family_tasks.tracker.utils.TestUtils.randomInt;
import static com.family_tasks.tracker.utils.TestUtils.randomString;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link TaskController}
 */
public class TaskUpdateControllerTest extends AbstractIntegrationTest {

    @Autowired
    TaskRepository taskRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    GroupRepository groupRepository;

    @EnumSource(value = TaskPriority.class)
    @ParameterizedTest
    void whenTaskExistWithGivenPriority_updateTask(TaskPriority taskPriority) {
        //prepare
        UserEntity reporter = createUser();
        TaskEntity taskEntity = buildTaskEntity(reporter.getId());
        taskRepository.save(taskEntity);
        String taskId = taskEntity.getId();
        TaskUpdateApiRequest request = buildUpdateRequest()
                .priority(taskPriority.name())
                .build();
        //execute
        ResponseEntity<TaskApiResponse> responseEntity = client.exchange(TaskController.TASK_URL + "/" + taskId,
                HttpMethod.PUT,
                new HttpEntity<>(request),
                TaskApiResponse.class);
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
        assertThat(response.getRewardsPoints()).isEqualTo(request.getRewardsPoints());
    }

    @EnumSource(value = TaskStatus.class)
    @ParameterizedTest
    void whenTaskExistWithGivenStatus_updateTask(TaskStatus taskStatus) {
        //prepare
        UserEntity reporter = createUser();
        TaskEntity taskEntity = buildTaskEntity(reporter.getId());
        taskRepository.save(taskEntity);
        String taskId = taskEntity.getId();
        TaskUpdateApiRequest request = buildUpdateRequest()
                .status(taskStatus.name())
                .build();
        //execute
        ResponseEntity<TaskApiResponse> responseEntity = client.exchange(TaskController.TASK_URL + "/" + taskId,
                HttpMethod.PUT,
                new HttpEntity<>(request),
                TaskApiResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        TaskApiResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.getStatus().name()).isEqualTo(request.getStatus());
        assertThat(response.getName()).isEqualTo(request.getName());
        assertThat(response.getDescription()).isEqualTo(request.getDescription());
        assertThat(response.getPriority().name()).isEqualTo(request.getPriority());
        assertThat(response.getExecutorIds()).isEqualTo(request.getExecutorIds());
        assertThat(response.isConfidential()).isEqualTo(request.getConfidential());
        assertThat(response.getDeadline()).isEqualTo(request.getDeadline());
        assertThat(response.getRewardsPoints()).isEqualTo(request.getRewardsPoints());
    }

    @Test
    void whenPriorityInvalid_updateTask() {
        //prepare
        UserEntity reporter = createUser();
        TaskEntity taskEntity = buildTaskEntity(reporter.getId());
        taskRepository.save(taskEntity);
        String taskId = taskEntity.getId();
        TaskUpdateApiRequest request = buildUpdateRequest()
                .priority("INVALID_PRIORITY")
                .build();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.exchange(TaskController.TASK_URL + "/" + taskId,
                HttpMethod.PUT,
                new HttpEntity<>(request),
                ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(TASK_PRIORITY_INVALID);
    }

    @Test
    void whenExecutorsFromAnotherGroup_updateTask() {
        //prepare
        UserEntity reporter = createUser();
        Integer groupId = createGroup(reporter.getId());
        reporter.setGroupId(groupId);
        userRepository.save(reporter);

        TaskEntity taskEntity = buildTaskEntity(reporter.getId());
        taskRepository.save(taskEntity);
        String taskId = taskEntity.getId();

        UserEntity executor = createUser();
        Integer executorGroupId = createGroup(executor.getId());
        executor.setGroupId(executorGroupId);
        userRepository.save(executor);

        TaskUpdateApiRequest request = buildUpdateRequest()
                .executorIds(Set.of(executor.getId()))
                .build();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.exchange(TaskController.TASK_URL + "/" + taskId,
                HttpMethod.PUT,
                new HttpEntity<>(request),
                ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(CREATE_OR_UPDATE_TASK_FOR_OWN_GROUP);
    }

    @Test
    void whenPriorityNull_updateTask() {
        //prepare
        UserEntity reporter = createUser();
        Integer groupId = createGroup(reporter.getId());
        reporter.setGroupId(groupId);
        userRepository.save(reporter);

        TaskEntity taskEntity = buildTaskEntity(reporter.getId());
        taskRepository.save(taskEntity);
        String taskId = taskEntity.getId();

        TaskUpdateApiRequest request = buildUpdateRequest()
                .priority(null)
                .build();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.exchange(TaskController.TASK_URL + "/" + taskId,
                HttpMethod.PUT,
                new HttpEntity<>(request),
                ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(TASK_PRIORITY_NULL);
    }

    @Test
    void whenPriorityEmpty_updateTask() {
        //prepare
        UserEntity reporter = createUser();
        Integer groupId = createGroup(reporter.getId());
        reporter.setGroupId(groupId);
        userRepository.save(reporter);

        TaskEntity taskEntity = buildTaskEntity(reporter.getId());
        taskRepository.save(taskEntity);
        String taskId = taskEntity.getId();
        TaskUpdateApiRequest request = buildUpdateRequest()
                .priority("")
                .build();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.exchange(TaskController.TASK_URL + "/" + taskId,
                HttpMethod.PUT,
                new HttpEntity<>(request),
                ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(TASK_PRIORITY_NULL);
    }

    @Test
    void whenEmptyTaskName_updateTask() {
        //prepare
        UserEntity reporter = createUser();
        Integer groupId = createGroup(reporter.getId());
        reporter.setGroupId(groupId);
        userRepository.save(reporter);

        TaskEntity taskEntity = buildTaskEntity(reporter.getId());
        taskRepository.save(taskEntity);
        String taskId = taskEntity.getId();
        TaskUpdateApiRequest request = buildUpdateRequest()
                .name("")
                .build();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.exchange(TaskController.TASK_URL + "/" + taskId,
                HttpMethod.PUT,
                new HttpEntity<>(request),
                ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(TASK_NAME_NOT_SPECIFIED);
    }

    @Test
    void whenTaskNameNull_updateTask() {
        //prepare
        UserEntity reporter = createUser();
        Integer groupId = createGroup(reporter.getId());
        reporter.setGroupId(groupId);
        userRepository.save(reporter);

        TaskEntity taskEntity = buildTaskEntity(reporter.getId());
        taskRepository.save(taskEntity);
        String taskId = taskEntity.getId();
        TaskUpdateApiRequest request = buildUpdateRequest()
                .name(null)
                .build();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.exchange(TaskController.TASK_URL + "/" + taskId,
                HttpMethod.PUT,
                new HttpEntity<>(request),
                ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(TASK_NAME_NOT_SPECIFIED);
    }

    @Test
    void whenTaskNameToLong_updateTask() {
        UserEntity reporter = createUser();
        Integer groupId = createGroup(reporter.getId());
        reporter.setGroupId(groupId);
        userRepository.save(reporter);

        TaskEntity taskEntity = buildTaskEntity(reporter.getId());
        taskRepository.save(taskEntity);
        String taskId = taskEntity.getId();
        TaskUpdateApiRequest request = buildUpdateRequest()
                .name(randomString(TASK_NAME_MAX_LENGTH + 1))
                .build();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.exchange(TaskController.TASK_URL + "/" + taskId,
                HttpMethod.PUT,
                new HttpEntity<>(request),
                ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(TASK_NAME_TOO_LONG);
    }

    @Test
    void whenTaskDescriptionToLong_updateTask() {
        UserEntity reporter = createUser();
        Integer groupId = createGroup(reporter.getId());
        reporter.setGroupId(groupId);
        userRepository.save(reporter);

        TaskEntity taskEntity = buildTaskEntity(reporter.getId());
        taskRepository.save(taskEntity);
        String taskId = taskEntity.getId();
        TaskUpdateApiRequest request = buildUpdateRequest()
                .description(randomString(TASK_DESCRIPTION_MAX_LENGTH + 1))
                .build();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.exchange(TaskController.TASK_URL + "/" + taskId,
                HttpMethod.PUT,
                new HttpEntity<>(request),
                ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(TASK_DESCRIPTION_TOO_LONG);
    }

    @Test
    void whenTaskDescriptionToShort_updateTask() {
        UserEntity reporter = createUser();
        Integer groupId = createGroup(reporter.getId());
        reporter.setGroupId(groupId);
        userRepository.save(reporter);

        TaskEntity taskEntity = buildTaskEntity(reporter.getId());
        taskRepository.save(taskEntity);
        String taskId = taskEntity.getId();
        TaskUpdateApiRequest request = buildUpdateRequest()
                .description(randomString(TASK_DESCRIPTION_MIN_LENGTH - 1))
                .build();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.exchange(TaskController.TASK_URL + "/" + taskId,
                HttpMethod.PUT,
                new HttpEntity<>(request),
                ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(TASK_DESCRIPTION_TOO_SHORT);
    }

    @Test
    void whenTaskConfidentialNull_updateTask() {
        //prepare
        UserEntity reporter = createUser();
        Integer groupId = createGroup(reporter.getId());
        reporter.setGroupId(groupId);
        userRepository.save(reporter);

        TaskEntity taskEntity = buildTaskEntity(reporter.getId());
        taskRepository.save(taskEntity);
        String taskId = taskEntity.getId();
        TaskUpdateApiRequest request = buildUpdateRequest()
                .confidential(null)
                .build();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.exchange(TaskController.TASK_URL + "/" + taskId,
                HttpMethod.PUT,
                new HttpEntity<>(request),
                ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(TASK_CONFIDENTIAL_STATUS_NOT_SPECIFIED);
    }

    @Test
    void whenStatusInvalid_updateTask() {
        //prepare
        UserEntity reporter = createUser();
        Integer groupId = createGroup(reporter.getId());
        reporter.setGroupId(groupId);
        userRepository.save(reporter);

        TaskEntity taskEntity = buildTaskEntity(reporter.getId());
        taskRepository.save(taskEntity);
        String taskId = taskEntity.getId();
        TaskUpdateApiRequest request = buildUpdateRequest()
                .status("INVALID_STATUS")
                .build();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.exchange(TASK_URL + "/" + taskId,
                HttpMethod.PUT,
                new HttpEntity<>(request),
                ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(TASK_STATUS_INVALID);
    }

    @Test
    void whenStatusNull_updateTask() {
        //prepare
        UserEntity reporter = createUser();
        Integer groupId = createGroup(reporter.getId());
        reporter.setGroupId(groupId);
        userRepository.save(reporter);

        TaskEntity taskEntity = buildTaskEntity(reporter.getId());
        taskRepository.save(taskEntity);
        String taskId = taskEntity.getId();
        TaskUpdateApiRequest request = buildUpdateRequest()
                .status(null)
                .build();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.exchange(TaskController.TASK_URL + "/" + taskId,
                HttpMethod.PUT,
                new HttpEntity<>(request),
                ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(TASK_STATUS_NULL);
    }

    @Test
    void whenStatusEmpty_updateTask() {
        //prepare
        UserEntity reporter = createUser();
        Integer groupId = createGroup(reporter.getId());
        reporter.setGroupId(groupId);
        userRepository.save(reporter);

        TaskEntity taskEntity = buildTaskEntity(reporter.getId());
        taskRepository.save(taskEntity);
        String taskId = taskEntity.getId();
        TaskUpdateApiRequest request = buildUpdateRequest()
                .status("")
                .build();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.exchange(TaskController.TASK_URL + "/" + taskId,
                HttpMethod.PUT,
                new HttpEntity<>(request),
                ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(TASK_STATUS_NULL);
    }

    @Test
    void WhenTaskNotExist_updateTask() {
        //prepare
        String taskId = UUID.randomUUID().toString();
        TaskUpdateApiRequest request = buildUpdateRequest().build();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.exchange(TaskController.TASK_URL + "/" + taskId,
                HttpMethod.PUT,
                new HttpEntity<>(request),
                ErrorResponse.class);
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
        UserEntity reporter = createUser();
        TaskEntity taskEntity = buildTaskEntity(reporter.getId());
        taskRepository.save(taskEntity);
        String taskId = taskEntity.getId();
        Integer executorId = createUser().getId() + 1;
        TaskUpdateApiRequest request = buildUpdateRequest()
                .executorIds(Set.of(executorId))
                .status("")
                .build();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.exchange(TaskController.TASK_URL + "/" + taskId,
                HttpMethod.PUT,
                new HttpEntity<>(request),
                ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(TASK_STATUS_NULL);
    }

    @Test
    void whenRewardsPointsZero_updateTask() {
        //prepare
        UserEntity reporter = createUser();
        Integer groupId = createGroup(reporter.getId());
        reporter.setGroupId(groupId);
        userRepository.save(reporter);

        TaskEntity taskEntity = buildTaskEntity(reporter.getId());
        taskRepository.save(taskEntity);
        String taskId = taskEntity.getId();
        TaskUpdateApiRequest request = buildUpdateRequest()
                .rewardsPoints(0)
                .build();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.exchange(TaskController.TASK_URL + "/" + taskId,
                HttpMethod.PUT,
                new HttpEntity<>(request),
                ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(REWARDS_POINTS_POSITIVE);
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

    private TaskUpdateApiRequest.TaskUpdateApiRequestBuilder buildUpdateRequest() {
        return TaskUpdateApiRequest.builder()
                .name("Name after update")
                .description("Description after update")
                .confidential(true)
                .deadline(LocalDate.now().plusDays(1))
                .executorIds(Set.of())
                .priority(TaskPriority.LOW.name())
                .status(TaskStatus.CANCELLED.name())
                .rewardsPoints(randomInt(1, 100));
    }

    private TaskEntity buildTaskEntity(Integer reporterId) {

        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(UUID.randomUUID().toString());
        taskEntity.setName("Name of task");
        taskEntity.setDescription("Description of task");
        taskEntity.setPriority((TaskPriority.HIGH.name()));
        taskEntity.setReporterId(reporterId);
        taskEntity.setExecutorIds(Set.of());
        taskEntity.setConfidential(false);
        taskEntity.setDeadline(LocalDate.now().plusDays(1));
        taskEntity.setCreatedAt(LocalDateTime.now());
        taskEntity.setUpdatedAt(LocalDateTime.now());
        taskEntity.setStatus(TaskStatus.TO_DO.name());
        taskEntity.setRewardsPoints(randomInt(1, 100));

        return taskEntity;
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