package com.family_tasks.tracker.task.application;

import com.family_tasks.tracker.AbstractIntegrationTest;
import com.family_tasks.tracker.common.error.ErrorResponse;
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
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import static com.family_tasks.tracker.common.validation.ValidationConstants.*;
import static com.family_tasks.tracker.common.validation.ValidationMessage.*;
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

    @EnumSource(value = TaskPriority.class)
    @ParameterizedTest
    void whenTaskExistWithGivenPriority_updateTask(TaskPriority taskPriority) {
        //prepare
        UserEntity reporter = createUserEntity();
        TaskEntity taskEntity = createTaskEntity(reporter.getId());
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
        UserEntity reporter = createUserEntity();
        TaskEntity taskEntity = createTaskEntity(reporter.getId());
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
    void whenExecutorsFromAnotherGroup_updateTask() {
        //prepare
        UserEntity reporter = createUserEntity();
        Integer groupId = createGroupEntity(reporter.getId());
        reporter.setGroupId(groupId);
        userRepository.save(reporter);

        TaskEntity taskEntity = createTaskEntity(reporter.getId());
        taskRepository.save(taskEntity);
        String taskId = taskEntity.getId();

        UserEntity executor = createUserEntity();
        Integer executorGroupId = createGroupEntity(executor.getId());
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

    @ParameterizedTest(name = "{index} => {0}")
    @MethodSource("invalidUpdateTaskProvider")
    void whenInvalidInput_updateTask(String testName,
                                     TaskUpdateApiRequest request,
                                     String expectedMessage) {
        UserEntity reporter = createUserEntity();
        Integer groupId = createGroupEntity(reporter.getId());
        reporter.setGroupId(groupId);
        userRepository.save(reporter);

        TaskEntity taskEntity = createTaskEntity(reporter.getId());
        taskRepository.save(taskEntity);
        String taskId = taskEntity.getId();

        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.exchange(TaskController.TASK_URL + "/" + taskId,
                HttpMethod.PUT,
                new HttpEntity<>(request),
                ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(expectedMessage);
    }

    private static Stream<Arguments> invalidUpdateTaskProvider() {

        return Stream.of(
                Arguments.of(
                        "When task's priority invalid",
                        buildUpdateRequest()
                                .priority("INVALID_PRIORITY")
                                .build(),
                        TASK_PRIORITY_INVALID
                ),
                Arguments.of(
                        "When task's priority null",
                        buildUpdateRequest()
                                .priority(null)
                                .build(),
                        TASK_PRIORITY_NULL
                ),
                Arguments.of(
                        "When task's priority empty",
                        buildUpdateRequest()
                                .priority("")
                                .build(),
                        TASK_PRIORITY_NULL
                ),
                Arguments.of(
                        "When task's name empty",
                        buildUpdateRequest()
                                .name("")
                                .build(),
                        TASK_NAME_NOT_SPECIFIED
                ),
                Arguments.of(
                        "When task's name null",
                        buildUpdateRequest()
                                .name(null)
                                .build(),
                        TASK_NAME_NOT_SPECIFIED
                ),
                Arguments.of(
                        "When task's name to long.",
                        buildUpdateRequest()
                                .name(randomString(TASK_NAME_MAX_LENGTH + 1))
                                .build(),
                        TASK_NAME_TOO_LONG
                ),
                Arguments.of(
                        "When task's description to long.",
                        buildUpdateRequest()
                                .description(randomString(TASK_DESCRIPTION_MAX_LENGTH + 1))
                                .build(),
                        TASK_DESCRIPTION_TOO_LONG
                ),
                Arguments.of(
                        "When task's description to short.",
                        buildUpdateRequest()
                                .description(randomString(TASK_DESCRIPTION_MIN_LENGTH - 1))
                                .build(),
                        TASK_DESCRIPTION_TOO_SHORT
                ),
                Arguments.of(
                        "When task's confidential status not specified.",
                        buildUpdateRequest()
                                .confidential(null)
                                .build(),
                        TASK_CONFIDENTIAL_STATUS_NOT_SPECIFIED
                ),
                Arguments.of(
                        "When task's status is invalid.",
                        buildUpdateRequest()
                                .status("INVALID_STATUS")
                                .build(),
                        TASK_STATUS_INVALID
                ),
                Arguments.of(
                        "When task's status is null.",
                        buildUpdateRequest()
                                .status(null)
                                .build(),
                        TASK_STATUS_NULL
                ),
                Arguments.of(
                        "When task's status is empty.",
                        buildUpdateRequest()
                                .status("")
                                .build(),
                        TASK_STATUS_NULL
                ),
                Arguments.of(
                        "When task's executor not exist.",
                        buildUpdateRequest()
                                .executorIds(Set.of(Integer.MAX_VALUE))
                                .build(),
                        String.format(USER_NOT_EXIST, Integer.MAX_VALUE)
                ),
                Arguments.of(
                        "When task's rewards point zero.",
                        buildUpdateRequest()
                                .rewardsPoints(0)
                                .build(),
                        REWARDS_POINTS_POSITIVE)
        );
    }

    private static TaskUpdateApiRequest.TaskUpdateApiRequestBuilder buildUpdateRequest() {
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
}