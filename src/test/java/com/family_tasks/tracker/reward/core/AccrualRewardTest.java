package com.family_tasks.tracker.reward.core;

import com.family_tasks.tracker.AbstractIntegrationTest;
import com.family_tasks.tracker.reward.infrastructure.RewardRepository;
import com.family_tasks.tracker.reward.model.entity.RewardEntity;
import com.family_tasks.tracker.task.application.TaskController;
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
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.family_tasks.tracker.utils.TestUtils.randomInt;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test for {@link com.family_tasks.tracker.reward.core.RewardAccrualService}
 */
public class AccrualRewardTest extends AbstractIntegrationTest {

    @Autowired
    TaskRepository taskRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RewardRepository rewardRepository;

    @Test
    void WhenTaskCompleted_addReward() {
        //prepare
        UserEntity user = createUserEntity();
        Integer groupId = createGroupEntity(user.getId());
        user.setGroupId(groupId);
        userRepository.save(user);

        TaskEntity taskEntity = createTaskEntity(user.getId());
        taskRepository.save(taskEntity);
        String taskId = taskEntity.getId();

        TaskUpdateApiRequest updateApiRequest = buildUpdateRequest()
                .executorIds(Set.of(user.getId()))
                .status(TaskStatus.COMPLETED.name())
                .build();
        //execute
        ResponseEntity<TaskApiResponse> responseEntity = client.exchange(TaskController.TASK_URL + "/" + taskId, HttpMethod.PUT, new HttpEntity<>(updateApiRequest), TaskApiResponse.class);
        List<RewardEntity> rewardEntityList = rewardRepository.findByTaskId(taskId);
        //validate
        TaskApiResponse taskApiResponse = responseEntity.getBody();
        RewardEntity rewardEntity = rewardEntityList.getFirst();

        assertThat(rewardEntity.getTotalSum()).isEqualTo(taskApiResponse.getRewardsPoints());
        assertThat(rewardEntity.getUserId()).isEqualTo(taskApiResponse.getExecutorIds().stream().findFirst().orElse(null));

        checkFieldsForTaskResponseAndRewardEntity(taskApiResponse, rewardEntity);
    }

    @Test
    void WhenTaskHasManyExecutors_addReward() {
        //prepare
        UserEntity user = createUserEntity();
        Integer groupId = createGroupEntity(user.getId());
        user.setGroupId(groupId);
        userRepository.save(user);

        Set<Integer> executorsIds = new HashSet<>();

        for (int i = 0; i < 10; i++) {
            UserEntity executorUser = createUserEntity();
            executorUser.setGroupId(groupId);
            userRepository.save(executorUser);
            executorsIds.add(executorUser.getId());
        }

        TaskEntity taskEntity = createTaskEntity(user.getId());
        taskRepository.save(taskEntity);
        String taskId = taskEntity.getId();

        TaskUpdateApiRequest updateApiRequest = buildUpdateRequest()
                .executorIds(executorsIds)
                .status(TaskStatus.COMPLETED.name())
                .build();
        //execute
        ResponseEntity<TaskApiResponse> responseEntity = client.exchange(TaskController.TASK_URL + "/" + taskId, HttpMethod.PUT, new HttpEntity<>(updateApiRequest), TaskApiResponse.class);
        List<RewardEntity> rewardEntityList = rewardRepository.findByTaskId(taskId);
        //validate
        assertThat(
                rewardEntityList.stream()
                        .map(RewardEntity::getUserId)
                        .toList())
                .containsAll(executorsIds);

        TaskApiResponse taskApiResponse = responseEntity.getBody();
        RewardEntity rewardEntity = rewardEntityList.getFirst();

        assertThat(rewardEntity.getTotalSum()).isEqualTo(taskApiResponse.getRewardsPoints());

        checkFieldsForTaskResponseAndRewardEntity(taskApiResponse, rewardEntity);
    }

    @Test
    void whenTwoTasksCompleted_addReward() {
        UserEntity user = createUserEntity();
        Integer groupId = createGroupEntity(user.getId());
        user.setGroupId(groupId);
        userRepository.save(user);

        TaskEntity taskEntity1 = createTaskEntity(user.getId());
        taskRepository.save(taskEntity1);
        String taskId1 = taskEntity1.getId();

        TaskUpdateApiRequest updateApiRequest1 = buildUpdateRequest()
                .executorIds(Set.of(user.getId()))
                .status(TaskStatus.COMPLETED.name())
                .build();

        TaskEntity taskEntity2 = createTaskEntity(user.getId());
        taskRepository.save(taskEntity2);
        String taskId2 = taskEntity2.getId();

        TaskUpdateApiRequest updateApiRequest2 = buildUpdateRequest()
                .executorIds(Set.of(user.getId()))
                .status(TaskStatus.COMPLETED.name())
                .build();
        //execute
        ResponseEntity<TaskApiResponse> responseEntity1 = client.exchange(TaskController.TASK_URL + "/" + taskId1, HttpMethod.PUT, new HttpEntity<>(updateApiRequest1), TaskApiResponse.class);
        ResponseEntity<TaskApiResponse> responseEntity2 = client.exchange(TaskController.TASK_URL + "/" + taskId2, HttpMethod.PUT, new HttpEntity<>(updateApiRequest2), TaskApiResponse.class);
        List<RewardEntity> rewardEntityList1 = rewardRepository.findByTaskId(taskId1);
        List<RewardEntity> rewardEntityList2 = rewardRepository.findByTaskId(taskId2);
        //validate
        TaskApiResponse taskApiResponse1 = responseEntity1.getBody();
        TaskApiResponse taskApiResponse2 = responseEntity2.getBody();
        RewardEntity rewardEntity1 = rewardEntityList1.getFirst();
        RewardEntity rewardEntity2 = rewardEntityList2.getFirst();

        assertThat(rewardEntity1.getTotalSum()).isEqualTo((taskApiResponse1.getRewardsPoints()));
        assertThat(rewardEntity2.getTotalSum()).isEqualTo((taskApiResponse1.getRewardsPoints() + taskApiResponse2.getRewardsPoints()));

        checkFieldsForTaskResponseAndRewardEntity(taskApiResponse1, rewardEntity1);
        checkFieldsForTaskResponseAndRewardEntity(taskApiResponse2, rewardEntity2);
    }

    @Test
    void WhenOneTaskCompletedTwiceForSameUser_addReward() {
        //prepare
        UserEntity user = createUserEntity();
        Integer groupId = createGroupEntity(user.getId());
        user.setGroupId(groupId);
        userRepository.save(user);

        TaskEntity taskEntity = createTaskEntity(user.getId());
        taskRepository.save(taskEntity);
        String taskId = taskEntity.getId();

        createRewardEntity(taskEntity);

        TaskUpdateApiRequest updateApiRequest = buildUpdateRequest()
                .executorIds(Set.of(user.getId()))
                .status(TaskStatus.COMPLETED.name())
                .build();
        //execute
        ResponseEntity<TaskApiResponse> responseEntity = client.exchange(TaskController.TASK_URL + "/" + taskId, HttpMethod.PUT, new HttpEntity<>(updateApiRequest), TaskApiResponse.class);
        List<RewardEntity> rewardEntityList = rewardRepository.findByTaskId(taskId);
        //validate
        TaskApiResponse taskApiResponse = responseEntity.getBody();
        RewardEntity rewardEntity = rewardEntityList.getFirst();

        assertThat(rewardEntityList.size()).isEqualTo(1);
        assertThat(rewardEntity.getTotalSum()).isEqualTo(taskApiResponse.getRewardsPoints());
        assertThat(rewardEntity.getUserId()).isEqualTo(taskApiResponse.getExecutorIds().stream().findFirst().orElse(null));

        checkFieldsForTaskResponseAndRewardEntity(taskApiResponse, rewardEntity);
    }

    @Test
    void WhenOneTaskCompletedTwiceForAnotherUser_addReward() {
        //prepare
        UserEntity user_1 = createUserEntity();
        Integer groupId = createGroupEntity(user_1.getId());
        user_1.setGroupId(groupId);
        userRepository.save(user_1);

        TaskEntity taskEntity = createTaskEntity(user_1.getId());
        taskRepository.save(taskEntity);
        String taskId = taskEntity.getId();

        RewardEntity rewardEntity = createRewardEntity(taskEntity);
        rewardRepository.save(rewardEntity);

        UserEntity user_2 = createUserEntity();
        user_2.setGroupId(groupId);
        userRepository.save(user_2);

        TaskUpdateApiRequest updateApiRequest = buildUpdateRequest()
                .executorIds(Set.of(user_1.getId(), user_2.getId()))
                .status(TaskStatus.COMPLETED.name())
                .rewardsPoints(taskEntity.getRewardsPoints())
                .build();
        //execute
        ResponseEntity<TaskApiResponse> responseEntity = client.exchange(TaskController.TASK_URL + "/" + taskId, HttpMethod.PUT, new HttpEntity<>(updateApiRequest), TaskApiResponse.class);
        List<RewardEntity> rewardEntityList = rewardRepository.findByTaskId(taskId);
        //validate
        assertThat(rewardEntityList.size()).isEqualTo(2);
        TaskApiResponse taskApiResponse = responseEntity.getBody();
        for (RewardEntity entity : rewardEntityList) {
            assertThat(entity.getTotalSum()).isEqualTo(taskApiResponse.getRewardsPoints());
            assertThat(taskApiResponse.getExecutorIds()).contains(entity.getUserId());
            checkFieldsForTaskResponseAndRewardEntity(taskApiResponse, entity);
        }
    }

    @EnumSource(value = TaskStatus.class)
    @ParameterizedTest
    void whenTaskNotCompleted_addReward(TaskStatus taskStatus) {
        if (!taskStatus.equals(TaskStatus.COMPLETED)) {
            //prepare
            UserEntity user = createUserEntity();
            Integer groupId = createGroupEntity(user.getId());
            user.setGroupId(groupId);
            userRepository.save(user);

            TaskEntity taskEntity = createTaskEntity(user.getId());
            taskRepository.save(taskEntity);
            String taskId = taskEntity.getId();

            TaskUpdateApiRequest updateApiRequest = buildUpdateRequest()
                    .executorIds(Set.of(user.getId()))
                    .status(taskStatus.name())
                    .build();
            //execute
            ResponseEntity<TaskApiResponse> responseEntity = client.exchange(TaskController.TASK_URL + "/" + taskId, HttpMethod.PUT, new HttpEntity<>(updateApiRequest), TaskApiResponse.class);
            //validate
            assertThat(rewardRepository.existByTaskIdAndUserId(taskId, user.getId())).isFalse();
        }
    }

    @Test
    void WhenRewardsPointsNull_addReward() {
        //prepare
        UserEntity user = createUserEntity();
        Integer groupId = createGroupEntity(user.getId());
        user.setGroupId(groupId);
        userRepository.save(user);

        TaskEntity taskEntity = createTaskEntity(user.getId());
        taskRepository.save(taskEntity);
        String taskId = taskEntity.getId();

        TaskUpdateApiRequest updateApiRequest = buildUpdateRequest()
                .executorIds(Set.of(user.getId()))
                .status(TaskStatus.COMPLETED.name())
                .rewardsPoints(null)
                .build();
        //execute
        ResponseEntity<TaskApiResponse> responseEntity = client.exchange(TaskController.TASK_URL + "/" + taskId, HttpMethod.PUT, new HttpEntity<>(updateApiRequest), TaskApiResponse.class);
        //validate
        assertThat(rewardRepository.existByTaskIdAndUserId(taskId, user.getId())).isFalse();
    }

    @Test
    void WhenExecutorsNull_addReward() {
        //prepare
        UserEntity user = createUserEntity();
        Integer groupId = createGroupEntity(user.getId());
        user.setGroupId(groupId);
        userRepository.save(user);

        TaskEntity taskEntity = createTaskEntity(user.getId());
        taskRepository.save(taskEntity);
        String taskId = taskEntity.getId();

        TaskUpdateApiRequest updateApiRequest = buildUpdateRequest()
                .executorIds(Set.of())
                .status(TaskStatus.COMPLETED.name())
                .build();
        //execute
        ResponseEntity<TaskApiResponse> responseEntity = client.exchange(TaskController.TASK_URL + "/" + taskId, HttpMethod.PUT, new HttpEntity<>(updateApiRequest), TaskApiResponse.class);
        //validate
        assertThat(rewardRepository.existByTaskIdAndUserId(taskId, user.getId())).isFalse();
    }

    private void checkFieldsForTaskResponseAndRewardEntity(TaskApiResponse taskApiResponse, RewardEntity rewardEntity) {
        assertThat(rewardEntity.getTaskId()).isEqualTo(taskApiResponse.getTaskId());
        assertThat(rewardEntity.getAmount()).isEqualTo(taskApiResponse.getRewardsPoints());
        assertThat(rewardEntity.getDescription()).isNull();
        assertThat(rewardEntity.getUpdatedBy()).isNull();
        assertThat(rewardEntity.getCreatedAt()).isNotNull();
        assertThat(rewardEntity.getUpdatedAt()).isNotNull();
    }

    private TaskUpdateApiRequest.TaskUpdateApiRequestBuilder buildUpdateRequest() {
        return TaskUpdateApiRequest.builder()
                .name("Name after update")
                .description("Description after update")
                .confidential(true)
                .deadline(LocalDate.now().plusDays(1))
                .executorIds(Set.of())
                .priority(TaskPriority.LOW.name())
                .status(TaskStatus.TO_DO.name())
                .rewardsPoints(randomInt(1, 100));
    }
}