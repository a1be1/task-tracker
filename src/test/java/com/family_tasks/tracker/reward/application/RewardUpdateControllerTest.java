package com.family_tasks.tracker.reward.application;

import com.family_tasks.tracker.AbstractIntegrationTest;
import com.family_tasks.tracker.common.error.ErrorResponse;
import com.family_tasks.tracker.common.utils.TimeUtils;
import com.family_tasks.tracker.group.infrastructure.GroupRepository;
import com.family_tasks.tracker.group.model.entity.GroupEntity;
import com.family_tasks.tracker.reward.infrastructure.RewardRepository;
import com.family_tasks.tracker.reward.model.dto.RewardApiResponse;
import com.family_tasks.tracker.reward.model.dto.RewardUpdateApiRequest;
import com.family_tasks.tracker.reward.model.entity.RewardEntity;
import com.family_tasks.tracker.task.infrastructure.TaskRepository;
import com.family_tasks.tracker.task.model.entity.TaskEntity;
import com.family_tasks.tracker.task.model.enums.TaskPriority;
import com.family_tasks.tracker.task.model.enums.TaskStatus;
import com.family_tasks.tracker.user.infrastructure.UserRepository;
import com.family_tasks.tracker.user.model.entity.UserEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import static com.family_tasks.tracker.common.validation.ValidationConstants.REWARD_DESCRIPTION_MAX_LENGTH;
import static com.family_tasks.tracker.common.validation.ValidationConstants.REWARD_DESCRIPTION_MIN_LENGTH;
import static com.family_tasks.tracker.common.validation.ValidationMessage.*;
import static com.family_tasks.tracker.utils.TestUtils.randomInt;
import static com.family_tasks.tracker.utils.TestUtils.randomString;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Tests for {@link RewardController}
 */
public class RewardUpdateControllerTest extends AbstractIntegrationTest {
    @Autowired
    TaskRepository taskRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RewardRepository rewardRepository;
    @Autowired
    GroupRepository groupRepository;

    @Test
    void whenRewardExist_updateReward() {
        //prepare
        UserEntity admin = createUser();
        Integer groupId = createGroup(admin.getId());
        admin.setAdmin(true);
        admin.setGroupId(groupId);
        userRepository.save(admin);

        UserEntity user = createUser();
        user.setGroupId(groupId);
        userRepository.save(user);

        TaskEntity taskEntity = buildCompletedTaskEntity(user.getId());
        taskRepository.save(taskEntity);

        RewardEntity rewardEntity = buildRewardEntity(taskEntity);
        rewardRepository.save(rewardEntity);

        RewardUpdateApiRequest request = RewardUpdateApiRequest.builder()
                .updatedBy(admin.getId())
                .description("description after update")
                .amount(0)
                .build();
        //execute
        ResponseEntity<RewardApiResponse> responseEntity = client.exchange(RewardController.REWARD_URL + "/" + rewardEntity.getId(),
                HttpMethod.PUT,
                new HttpEntity<>(request),
                RewardApiResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        RewardApiResponse response = responseEntity.getBody();
        Assertions.assertThat(response).isNotNull();
        assertThat(response.getRewardId()).isEqualTo(rewardEntity.getId());
        assertThat(response.getUserId()).isEqualTo(rewardEntity.getUserId());
        assertThat(response.getTaskId()).isEqualTo(rewardEntity.getTaskId());
        assertThat(response.getUpdatedBy()).isEqualTo(request.getUpdatedBy());
        assertThat(response.getDescription()).isEqualTo(request.getDescription());
        assertThat(response.getAmount()).isEqualTo(request.getAmount());
        assertThat(response.getTotalSum()).isEqualTo(0);
        assertThat(response.getCreatedAt()).isNotNull();
        assertThat(response.getUpdatedAt()).isNotNull();
    }

    @Test
    void whenTwoRewardsExist_updateReward() {
        //prepare
        UserEntity admin = createUser();
        Integer groupId = createGroup(admin.getId());
        admin.setAdmin(true);
        admin.setGroupId(groupId);
        userRepository.save(admin);

        UserEntity user = createUser();
        user.setGroupId(groupId);
        userRepository.save(user);

        TaskEntity taskEntity1 = buildCompletedTaskEntity(user.getId());
        taskRepository.save(taskEntity1);

        RewardEntity rewardEntity1 = buildRewardEntity(taskEntity1);
        rewardRepository.save(rewardEntity1);

        TaskEntity taskEntity2 = buildCompletedTaskEntity(user.getId());
        taskRepository.save(taskEntity2);

        RewardEntity rewardEntity2 = buildRewardEntity(taskEntity2);
        int rewardDelta = rewardEntity2.getAmount();
        rewardEntity2.setTotalSum(rewardEntity2.getAmount() + rewardEntity1.getAmount());
        rewardRepository.save(rewardEntity2);

        RewardUpdateApiRequest request = RewardUpdateApiRequest.builder()
                .updatedBy(admin.getId())
                .description("description after update")
                .amount(0)
                .build();
        //execute
        ResponseEntity<RewardApiResponse> responseEntity = client.exchange(RewardController.REWARD_URL + "/" + rewardEntity1.getId(),
                HttpMethod.PUT,
                new HttpEntity<>(request),
                RewardApiResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        RewardApiResponse response = responseEntity.getBody();
        Assertions.assertThat(response).isNotNull();
        assertThat(response.getRewardId()).isEqualTo(rewardEntity1.getId());
        assertThat(response.getUserId()).isEqualTo(rewardEntity1.getUserId());
        assertThat(response.getTaskId()).isEqualTo(rewardEntity1.getTaskId());
        assertThat(response.getUpdatedBy()).isEqualTo(request.getUpdatedBy());
        assertThat(response.getDescription()).isEqualTo(request.getDescription());
        assertThat(response.getAmount()).isEqualTo(request.getAmount());
        assertThat(response.getTotalSum()).isEqualTo(0);
        assertThat(response.getCreatedAt()).isNotNull();
        assertThat(response.getUpdatedAt()).isNotNull();

        rewardEntity2 = rewardRepository.findById(rewardEntity2.getId()).orElse(null);
        org.junit.jupiter.api.Assertions.assertNotNull(rewardEntity2);
        assertThat(rewardEntity2.getTotalSum()).isEqualTo(rewardDelta);
    }

    @Test
    void whenTotalSumLessNull_updateReward() {
        //prepare
        UserEntity admin = createUser();
        Integer groupId = createGroup(admin.getId());
        admin.setAdmin(true);
        admin.setGroupId(groupId);
        userRepository.save(admin);

        UserEntity user = createUser();
        user.setGroupId(groupId);
        userRepository.save(user);

        TaskEntity taskEntity1 = buildCompletedTaskEntity(user.getId());
        taskRepository.save(taskEntity1);

        RewardEntity rewardEntity1 = buildRewardEntity(taskEntity1);
        rewardRepository.save(rewardEntity1);

        TaskEntity taskEntity2 = buildCompletedTaskEntity(user.getId());
        taskRepository.save(taskEntity2);

        RewardEntity rewardEntity2 = buildRewardEntity(taskEntity2);
        rewardEntity2.setTotalSum(0);
        rewardRepository.save(rewardEntity2);

        RewardUpdateApiRequest request = RewardUpdateApiRequest.builder()
                .updatedBy(admin.getId())
                .description("description after update")
                .amount(0)
                .build();
        //execute
        ResponseEntity<RewardApiResponse> responseEntity = client.exchange(RewardController.REWARD_URL + "/" + rewardEntity1.getId(),
                HttpMethod.PUT,
                new HttpEntity<>(request),
                RewardApiResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        RewardApiResponse response = responseEntity.getBody();
        Assertions.assertThat(response).isNotNull();
        assertThat(response.getRewardId()).isEqualTo(rewardEntity1.getId());
        assertThat(response.getUserId()).isEqualTo(rewardEntity1.getUserId());
        assertThat(response.getTaskId()).isEqualTo(rewardEntity1.getTaskId());
        assertThat(response.getUpdatedBy()).isEqualTo(request.getUpdatedBy());
        assertThat(response.getDescription()).isEqualTo(request.getDescription());
        assertThat(response.getAmount()).isEqualTo(request.getAmount());
        assertThat(response.getTotalSum()).isEqualTo(0);
        assertThat(response.getCreatedAt()).isNotNull();
        assertThat(response.getUpdatedAt()).isNotNull();

        rewardEntity2 = rewardRepository.findById(rewardEntity2.getId()).orElse(null);
        org.junit.jupiter.api.Assertions.assertNotNull(rewardEntity2);
        assertThat(rewardEntity2.getTotalSum()).isEqualTo(0);
    }

    @Test
    void whenRewardNotExist_updateReward() {
        //prepare
        UserEntity admin = createUser();
        Integer groupId = createGroup(admin.getId());
        admin.setGroupId(groupId);
        userRepository.save(admin);

        UserEntity user = createUser();
        user.setGroupId(groupId);
        userRepository.save(user);

        String rewardEntityId = UUID.randomUUID().toString();

        RewardUpdateApiRequest request = RewardUpdateApiRequest.builder()
                .updatedBy(admin.getId())
                .description("description after update")
                .amount(0)
                .build();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.exchange(RewardController.REWARD_URL + "/" + rewardEntityId,
                HttpMethod.PUT,
                new HttpEntity<>(request),
                ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        org.junit.jupiter.api.Assertions.assertNotNull(response);
        assertThat(response.errorMessage()).isEqualTo(String.format(REWARD_NOT_EXIST, rewardEntityId));
    }

    @Test
    void whenUpdatedByNotAdmin_updateReward() {
        //prepare
        UserEntity admin = createUser();
        Integer groupId = createGroup(admin.getId());
        admin.setGroupId(groupId);
        userRepository.save(admin);

        UserEntity user = createUser();
        user.setGroupId(groupId);
        userRepository.save(user);

        TaskEntity taskEntity = buildCompletedTaskEntity(user.getId());
        taskRepository.save(taskEntity);

        RewardEntity rewardEntity = buildRewardEntity(taskEntity);
        rewardRepository.save(rewardEntity);

        RewardUpdateApiRequest request = RewardUpdateApiRequest.builder()
                .updatedBy(admin.getId())
                .description("description after update")
                .amount(0)
                .build();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.exchange(RewardController.REWARD_URL + "/" + rewardEntity.getId(),
                HttpMethod.PUT,
                new HttpEntity<>(request),
                ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        org.junit.jupiter.api.Assertions.assertNotNull(response);
        assertThat(response.errorMessage()).isEqualTo(USER_NOT_ADMIN);
    }

    @Test
    void whenUpdatedByFromAnotherGroup_updateReward() {
        //prepare
        UserEntity admin = createUser();
        Integer groupId1 = createGroup(admin.getId());
        admin.setAdmin(true);
        admin.setGroupId(groupId1);
        userRepository.save(admin);

        UserEntity user = createUser();
        Integer groupId2 = createGroup(user.getId());
        user.setGroupId(groupId2);
        userRepository.save(user);

        TaskEntity taskEntity = buildCompletedTaskEntity(user.getId());
        taskRepository.save(taskEntity);

        RewardEntity rewardEntity = buildRewardEntity(taskEntity);
        rewardRepository.save(rewardEntity);

        RewardUpdateApiRequest request = RewardUpdateApiRequest.builder()
                .updatedBy(admin.getId())
                .description("description after update")
                .amount(0)
                .build();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.exchange(RewardController.REWARD_URL + "/" + rewardEntity.getId(),
                HttpMethod.PUT,
                new HttpEntity<>(request),
                ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        org.junit.jupiter.api.Assertions.assertNotNull(response);
        assertThat(response.errorMessage()).isEqualTo(UPDATE_REWARD_FOR_OWN_GROUP);
    }

    @Test
    void whenUpdatedByNotExist_updateReward() {
        //prepare
        UserEntity user = createUser();
        Integer groupId = createGroup(user.getId());
        user.setGroupId(groupId);
        userRepository.save(user);

        Integer adminId = user.getId() + 2;

        TaskEntity taskEntity = buildCompletedTaskEntity(user.getId());
        taskRepository.save(taskEntity);

        RewardEntity rewardEntity = buildRewardEntity(taskEntity);
        rewardRepository.save(rewardEntity);

        RewardUpdateApiRequest request = RewardUpdateApiRequest.builder()
                .updatedBy(adminId)
                .description("description after update")
                .amount(0)
                .build();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.exchange(RewardController.REWARD_URL + "/" + rewardEntity.getId(),
                HttpMethod.PUT,
                new HttpEntity<>(request),
                ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        org.junit.jupiter.api.Assertions.assertNotNull(response);
        assertThat(response.errorMessage()).isEqualTo(USER_NOT_EXIST, adminId);
    }

    @ParameterizedTest(name = "{index} => {0}")
    @MethodSource("invalidRewardUpdateData")
    void whenInvalidUpdateRequest_thenReturnBadRequest(
            String testName,
            RewardUpdateApiRequest request,
            String expectedError
    ) {
        // prepare
        UserEntity admin = createUser();
        Integer groupId = createGroup(admin.getId());
        admin.setAdmin(true);
        admin.setGroupId(groupId);
        userRepository.save(admin);

        UserEntity user = createUser();
        user.setGroupId(groupId);
        userRepository.save(user);

        TaskEntity taskEntity = buildCompletedTaskEntity(user.getId());
        taskRepository.save(taskEntity);

        RewardEntity rewardEntity = buildRewardEntity(taskEntity);
        rewardRepository.save(rewardEntity);

        // execute
        ResponseEntity<ErrorResponse> responseEntity = client.exchange(
                RewardController.REWARD_URL + "/" + rewardEntity.getId(),
                HttpMethod.PUT,
                new HttpEntity<>(request),
                ErrorResponse.class
        );

        // validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(expectedError);
    }

    private static Stream<Arguments> invalidRewardUpdateData() {
        return Stream.of(
                Arguments.of(
                        "Description is null",
                        RewardUpdateApiRequest.builder()
                                .updatedBy(1)
                                .description(null)
                                .amount(0)
                                .build(),
                        REWARD_DESCRIPTION_NULL
                ),
                Arguments.of(
                        "Description too short",
                        RewardUpdateApiRequest.builder()
                                .updatedBy(1)
                                .description(randomString(REWARD_DESCRIPTION_MIN_LENGTH - 1))
                                .amount(0)
                                .build(),
                        REWARD_DESCRIPTION_TOO_SHORT
                ),
                Arguments.of(
                        "Description too long",
                        RewardUpdateApiRequest.builder()
                                .updatedBy(1)
                                .description(randomString(REWARD_DESCRIPTION_MAX_LENGTH + 1))
                                .amount(0)
                                .build(),
                        REWARD_DESCRIPTION_TOO_LONG
                ),
                Arguments.of(
                        "Amount is null",
                        RewardUpdateApiRequest.builder()
                                .updatedBy(1)
                                .description("description after update")
                                .amount(null)
                                .build(),
                        REWARD_AMOUNT_NULL
                ),
                Arguments.of(
                        "Amount is negative",
                        RewardUpdateApiRequest.builder()
                                .updatedBy(1)
                                .description("description after update")
                                .amount(-1)
                                .build(),
                        REWARDS_POINTS_POSITIVE
                ),
                Arguments.of(
                        "UpdatedBy is null",
                        RewardUpdateApiRequest.builder()
                                .updatedBy(null)
                                .description("description after update")
                                .amount(0)
                                .build(),
                        USER_NOT_SPECIFIED
                )
        );
    }

    private RewardEntity buildRewardEntity(TaskEntity task) {
        RewardEntity rewardEntity = new RewardEntity();
        rewardEntity.setId(UUID.randomUUID().toString());
        rewardEntity.setTaskId(task.getId());
        rewardEntity.setUserId(task.getExecutorIds().stream().findFirst().orElse(null));
        rewardEntity.setTotalSum(task.getRewardsPoints());
        rewardEntity.setAmount(task.getRewardsPoints());
        rewardEntity.setCreatedAt(TimeUtils.now());
        rewardEntity.setUpdatedAt(TimeUtils.now());
        rewardEntity.setUpdatedBy(null);
        rewardEntity.setDescription(null);

        return rewardEntity;
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

    private TaskEntity buildCompletedTaskEntity(Integer userId) {

        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(UUID.randomUUID().toString());
        taskEntity.setName("Name of task");
        taskEntity.setDescription("Description of task");
        taskEntity.setPriority((TaskPriority.HIGH.name()));
        taskEntity.setReporterId(userId);
        taskEntity.setExecutorIds(Set.of(userId));
        taskEntity.setConfidential(false);
        taskEntity.setDeadline(LocalDate.from(TimeUtils.now().plusDays(1)));
        taskEntity.setCreatedAt(TimeUtils.now());
        taskEntity.setUpdatedAt(TimeUtils.now());
        taskEntity.setStatus(TaskStatus.COMPLETED.name());
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