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
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

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
    @Autowired
    GroupRepository groupRepository;

    @EnumSource(value = TaskPriority.class)
    @ParameterizedTest
    void withGivenPriority_createTask(TaskPriority priority) {
        //prepare
        UserEntity reporter = createUser();
        Integer groupId = createGroup(reporter.getId());
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
        UserEntity reporter = createUser();
        Integer groupId = createGroup(reporter.getId());
        reporter.setGroupId(groupId);
        userRepository.save(reporter);

        UserEntity executor = createUser();
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
        UserEntity user = createUser();
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
        UserEntity reporter = createUser();
        Integer groupId = createGroup(reporter.getId());
        reporter.setGroupId(groupId);
        userRepository.save(reporter);

        UserEntity executor = createUser();
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
        UserEntity reporter = createUser();
        Integer groupId = createGroup(reporter.getId());
        reporter.setGroupId(groupId);
        userRepository.save(reporter);

        UserEntity executor = createUser();
        Integer executorGroupId = createGroup(executor.getId());
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

    @Test
    void whenPriorityInvalid_createTask() {
        //prepare
        UserEntity reporter = createUser();
        Integer groupId = createGroup(reporter.getId());
        reporter.setGroupId(groupId);
        userRepository.save(reporter);

        TaskCreateApiRequest request = buildCreateRequest(reporter.getId())
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
        UserEntity reporter = createUser();
        Integer groupId = createGroup(reporter.getId());
        reporter.setGroupId(groupId);
        userRepository.save(reporter);

        TaskCreateApiRequest request = buildCreateRequest(reporter.getId())
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
        UserEntity reporter = createUser();
        Integer groupId = createGroup(reporter.getId());
        reporter.setGroupId(groupId);
        userRepository.save(reporter);

        TaskCreateApiRequest request = buildCreateRequest(reporter.getId())
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
        UserEntity reporter = createUser();
        Integer groupId = createGroup(reporter.getId());
        reporter.setGroupId(groupId);
        userRepository.save(reporter);

        TaskCreateApiRequest request = buildCreateRequest(reporter.getId())
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
        UserEntity reporter = createUser();
        Integer groupId = createGroup(reporter.getId());
        reporter.setGroupId(groupId);
        userRepository.save(reporter);

        TaskCreateApiRequest request = buildCreateRequest(reporter.getId())
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
        UserEntity reporter = createUser();
        Integer groupId = createGroup(reporter.getId());
        reporter.setGroupId(groupId);
        userRepository.save(reporter);

        TaskCreateApiRequest request = buildCreateRequest(reporter.getId())
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
        UserEntity reporter = createUser();
        Integer groupId = createGroup(reporter.getId());
        reporter.setGroupId(groupId);
        userRepository.save(reporter);

        TaskCreateApiRequest request = buildCreateRequest(reporter.getId())
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
        UserEntity reporter = createUser();
        Integer groupId = createGroup(reporter.getId());
        reporter.setGroupId(groupId);
        userRepository.save(reporter);

        TaskCreateApiRequest request = buildCreateRequest(reporter.getId())
                .deadline(LocalDate.now().minusDays(1))
                .build();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.postForEntity(TASK_URL, request, ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(TASK_DEADLINE_DATE_NOT_PRESENT_OR_FUTURE);
    }


    @Test
    void whenTaskConfidentialNull_createTask() {
        //prepare
        UserEntity reporter = createUser();
        Integer groupId = createGroup(reporter.getId());
        reporter.setGroupId(groupId);
        userRepository.save(reporter);

        TaskCreateApiRequest request = buildCreateRequest(reporter.getId())
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
        UserEntity reporter = createUser();
        Integer groupId = createGroup(reporter.getId());
        reporter.setGroupId(groupId);
        userRepository.save(reporter);

        TaskCreateApiRequest request = buildCreateRequest(reporter.getId())
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
        Integer reporterId = createUser().getId() + 2;
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
        UserEntity reporter = createUser();
        Integer groupId = createGroup(reporter.getId());
        reporter.setGroupId(groupId);
        userRepository.save(reporter);

        Integer executorId = createUser().getId() + 2;
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

    @Test
    void whenRewardPointsZero_createTask() {
        //prepare
        UserEntity reporter = createUser();
        Integer groupId = createGroup(reporter.getId());
        reporter.setGroupId(groupId);
        userRepository.save(reporter);

        Integer executorId = createUser().getId() + 2;
        TaskCreateApiRequest request = buildCreateRequest(reporter.getId())
                .rewardsPoints(0)
                .build();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.postForEntity(TASK_URL, request, ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(REWARDS_POINTS_POSITIVE);
    }

    private TaskCreateApiRequest.TaskCreateApiRequestBuilder buildCreateRequest(Integer userId) {
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

    private UserEntity createUser() {
        UserEntity userEntity = new UserEntity();
        userEntity.setName("user name");
        userEntity.setAdmin(false);
        userEntity.setCreatedAt(TimeUtils.now());
        userEntity.setUpdatedAt(TimeUtils.now());
        userRepository.save(userEntity);
        return userEntity;
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