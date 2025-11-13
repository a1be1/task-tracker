package com.family_tasks.tracker.task.application;

import com.family_tasks.tracker.AbstractIntegrationTest;
import com.family_tasks.tracker.common.error.ErrorResponse;
import com.family_tasks.tracker.common.utils.TimeUtils;
import com.family_tasks.tracker.group.infrastructure.GroupRepository;
import com.family_tasks.tracker.group.model.entity.GroupEntity;
import com.family_tasks.tracker.task.infrastructure.TaskRepository;
import com.family_tasks.tracker.task.model.dto.TaskApiResponse;
import com.family_tasks.tracker.task.model.dto.TaskCreateApiRequest;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Stream;

import static com.family_tasks.tracker.common.validation.ValidationConstants.*;
import static com.family_tasks.tracker.common.validation.ValidationMessage.*;
import static com.family_tasks.tracker.task.application.TaskController.TASK_URL;
import static com.family_tasks.tracker.utils.TestUtils.randomInt;
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

    @EnumSource(value = TaskPriority.class)
    @ParameterizedTest
    void withGivenPriority_createTask(TaskPriority priority) {
        //prepare
        UserEntity reporter = createUserEntity();
        Integer groupId = createGroupEntity(reporter.getId());
        reporter.setGroupId(groupId);
        userRepository.save(reporter);

        TaskCreateApiRequest request = buildCreateRequest(reporter.getId())
                .priority(priority.name())
                .build();
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
        assertThat(response.getRewardsPoints()).isEqualTo(request.getRewardsPoints());

        TaskEntity taskEntity = taskRepository.findById(response.getTaskId()).orElseThrow();
        assertThat(taskEntity).isNotNull();
    }

    @Test
    void whenExecutorWithSameGroup_createTask() {
        //prepare
        UserEntity reporter = createUserEntity();
        Integer groupId = createGroupEntity(reporter.getId());
        reporter.setGroupId(groupId);
        userRepository.save(reporter);

        UserEntity executor = createUserEntity();
        executor.setGroupId(groupId);
        userRepository.save(executor);

        TaskCreateApiRequest request = buildCreateRequest(reporter.getId())
                .executorIds(Set.of(executor.getId())).build();
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
        assertThat(response.getRewardsPoints()).isEqualTo(request.getRewardsPoints());

        TaskEntity taskEntity = taskRepository.findById(response.getTaskId()).orElseThrow();
        assertThat(taskEntity).isNotNull();
    }

    @Test
    void whenUserWithoutGroup_createTask() {
        //prepare
        UserEntity user = createUserEntity();
        TaskCreateApiRequest request = buildCreateRequest(user.getId()).build();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.postForEntity(TASK_URL, request, ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(CREATE_TASK_WITHOUT_GROUP);
    }

    @Test
    void whenExecutorWithoutGroup_createTask() {
        //prepare
        UserEntity reporter = createUserEntity();
        Integer groupId = createGroupEntity(reporter.getId());
        reporter.setGroupId(groupId);
        userRepository.save(reporter);

        UserEntity executor = createUserEntity();
        TaskCreateApiRequest request = buildCreateRequest(reporter.getId())
                .executorIds(Set.of(executor.getId())).build();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.postForEntity(TASK_URL, request, ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(CREATE_OR_UPDATE_TASK_FOR_OWN_GROUP);
    }

    @Test
    void whenExecutorWithAnotherGroup_createTask() {
        //prepare
        UserEntity reporter = createUserEntity();
        Integer groupId = createGroupEntity(reporter.getId());
        reporter.setGroupId(groupId);
        userRepository.save(reporter);

        UserEntity executor = createUserEntity();
        Integer executorGroupId = createGroupEntity(executor.getId());
        executor.setGroupId(executorGroupId);
        userRepository.save(executor);

        TaskCreateApiRequest request = buildCreateRequest(reporter.getId())
                .executorIds(Set.of(executor.getId())).build();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.postForEntity(TASK_URL, request, ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(CREATE_OR_UPDATE_TASK_FOR_OWN_GROUP);
    }

    @ParameterizedTest(name = "{index} => {0}")
    @MethodSource("invalidCreateTaskProvider")
    void whenInvalidInput_createTask_shouldReturnBadRequest(String testName,
                                                            TaskCreateApiRequest request,
                                                            String expectedMessage) {
        // execute
        ResponseEntity<ErrorResponse> responseEntity =
                client.postForEntity(TaskController.TASK_URL, request, ErrorResponse.class);

        // validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(expectedMessage);
    }

    @Test
    void whenTaskReporterNotExist_createTask() {
        //prepare
        Integer reporterId = createUserEntity().getId() + 2;
        TaskCreateApiRequest request = buildCreateRequest(reporterId)
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
        UserEntity reporter = createUserEntity();
        Integer groupId = createGroupEntity(reporter.getId());
        reporter.setGroupId(groupId);
        userRepository.save(reporter);

        Integer executorId = createUserEntity().getId() + 2;
        TaskCreateApiRequest request = buildCreateRequest(reporter.getId())
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

    private static Stream<Arguments> invalidCreateTaskProvider() {

        return Stream.of(
                Arguments.of(
                        "When invalid priority",
                        buildCreateRequest(1)
                                .priority("INVALID_PRIORITY")
                                .build(),
                        TASK_PRIORITY_INVALID),
                Arguments.of("When task's priority null",
                        buildCreateRequest(1)
                                .priority(null)
                                .build(),
                        TASK_PRIORITY_NULL),
                Arguments.of("When task's priority empty",
                        buildCreateRequest(1)
                                .priority("")
                                .build(),
                        TASK_PRIORITY_NULL),
                Arguments.of("When task's name null",
                        buildCreateRequest(1)
                                .name(null)
                                .build(),
                        TASK_NAME_NOT_SPECIFIED),
                Arguments.of("When task's name empty",
                        buildCreateRequest(1)
                                .name("")
                                .build(),
                        TASK_NAME_NOT_SPECIFIED),
                Arguments.of("When task's name to long",
                        buildCreateRequest(1)
                                .name(randomString(TASK_NAME_MAX_LENGTH + 1))
                                .build(),
                        TASK_NAME_TOO_LONG),
                Arguments.of("When task's description to long",
                        buildCreateRequest(1)
                                .description(randomString(TASK_DESCRIPTION_MAX_LENGTH + 1))
                                .build(),
                        TASK_DESCRIPTION_TOO_LONG),
                Arguments.of("When task's description to short",
                        buildCreateRequest(1)
                                .description(randomString(TASK_DESCRIPTION_MIN_LENGTH - 1))
                                .build(),
                        TASK_DESCRIPTION_TOO_SHORT),
                Arguments.of("when Deadline date invalid",
                        buildCreateRequest(1)
                                .deadline(LocalDate.now().minusDays(1))
                                .build(),
                        TASK_DEADLINE_DATE_NOT_PRESENT_OR_FUTURE),
                Arguments.of("When confidential is null",
                        buildCreateRequest(1)
                                .confidential(null)
                                .build(),
                        TASK_CONFIDENTIAL_STATUS_NOT_SPECIFIED),
                Arguments.of("When task's reporter is null",
                        buildCreateRequest(null)
                                .build(),
                        TASK_REPORTER_NULL),
                Arguments.of("When reward points are zero",
                        buildCreateRequest(1)
                                .rewardsPoints(0)
                                .build(),
                        REWARDS_POINTS_POSITIVE)
        );
    }

    private static TaskCreateApiRequest.TaskCreateApiRequestBuilder buildCreateRequest(Integer userId) {
        return TaskCreateApiRequest.builder()
                .name("Name of task")
                .description("Description of task")
                .priority(TaskPriority.HIGH.name())
                .reporterId(userId)
                .executorIds(Set.of())
                .confidential(false)
                .deadline(LocalDate.now())
                .rewardsPoints(randomInt(1, 100));
    }
}