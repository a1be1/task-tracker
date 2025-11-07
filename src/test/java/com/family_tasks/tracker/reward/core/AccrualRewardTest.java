package com.family_tasks.tracker.reward.core;

import com.family_tasks.tracker.AbstractIntegrationTest;
import com.family_tasks.tracker.group.infrastructure.GroupRepository;
import com.family_tasks.tracker.group.model.entity.GroupEntity;
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
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.family_tasks.tracker.utils.TestUtils.randomInt;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Test for {@link com.family_tasks.tracker.reward.core.RewardAccrualService}
 */
public class AccrualRewardTest extends AbstractIntegrationTest {

    @Autowired
    TaskRepository taskRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    RewardRepository rewardRepository;

    @Test
    void WhenTaskCompleted_addReward() {
        //prepare
        UserEntity user = createUser(null);
        Integer groupId = createGroup(user.getId());
        user.setGroupId(groupId);
        userRepository.save(user);

        TaskEntity taskEntity = buildTaskEntity(user.getId());
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
        UserEntity user = createUser(null);
        Integer groupId = createGroup(user.getId());
        user.setGroupId(groupId);
        userRepository.save(user);

        Set<Integer> executorsIds = new HashSet<>();

        for (int i = 0; i < 10; i++) {
            UserEntity executorUser = createUser(groupId);
            executorsIds.add(executorUser.getId());
        }

        TaskEntity taskEntity = buildTaskEntity(user.getId());
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
        assertThat(rewardEntityList.stream().map(RewardEntity::getUserId).toList().containsAll(executorsIds));

        TaskApiResponse taskApiResponse = responseEntity.getBody();
        RewardEntity rewardEntity = rewardEntityList.getFirst();

        assertThat(rewardEntity.getTotalSum()).isEqualTo(taskApiResponse.getRewardsPoints());

        checkFieldsForTaskResponseAndRewardEntity(taskApiResponse, rewardEntity);
    }

    @Test
    void whenTwoTasksCompleted_addReward() {
        UserEntity user = createUser(null);
        Integer groupId = createGroup(user.getId());
        user.setGroupId(groupId);
        userRepository.save(user);

        TaskEntity taskEntity1 = buildTaskEntity(user.getId());
        taskRepository.save(taskEntity1);
        String taskId1 = taskEntity1.getId();

        TaskUpdateApiRequest updateApiRequest1 = buildUpdateRequest()
                .executorIds(Set.of(user.getId()))
                .status(TaskStatus.COMPLETED.name())
                .build();

        TaskEntity taskEntity2 = buildTaskEntity(user.getId());
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
    void WhenOneTaskCompletedTwice_addReward() {
        //prepare
        UserEntity user = createUser(null);
        Integer groupId = createGroup(user.getId());
        user.setGroupId(groupId);
        userRepository.save(user);

        TaskEntity taskEntity = buildTaskEntity(user.getId());
        taskRepository.save(taskEntity);
        String taskId = taskEntity.getId();

        TaskUpdateApiRequest updateApiRequest = buildUpdateRequest()
                .executorIds(Set.of(user.getId()))
                .status(TaskStatus.COMPLETED.name())
                .build();
        //execute
        ResponseEntity<TaskApiResponse> responseEntity1 = client.exchange(TaskController.TASK_URL + "/" + taskId, HttpMethod.PUT, new HttpEntity<>(updateApiRequest), TaskApiResponse.class);
        ResponseEntity<TaskApiResponse> responseEntity2 = client.exchange(TaskController.TASK_URL + "/" + taskId, HttpMethod.PUT, new HttpEntity<>(updateApiRequest), TaskApiResponse.class);
        List<RewardEntity> rewardEntityList = rewardRepository.findByTaskId(taskId);
        //validate
        TaskApiResponse taskApiResponse = responseEntity2.getBody();
        RewardEntity rewardEntity = rewardEntityList.getFirst();

        assertThat(rewardEntityList.size()).isEqualTo(1);
        assertThat(rewardEntity.getTotalSum()).isEqualTo(taskApiResponse.getRewardsPoints());
        assertThat(rewardEntity.getUserId()).isEqualTo(taskApiResponse.getExecutorIds().stream().findFirst().orElse(null));

        checkFieldsForTaskResponseAndRewardEntity(taskApiResponse, rewardEntity);
    }

    @EnumSource(value = TaskStatus.class)
    @ParameterizedTest
    void whenTaskNotCompleted_addReward(TaskStatus taskStatus) {
        if (!taskStatus.equals(TaskStatus.COMPLETED)) {
            //prepare
            UserEntity user = createUser(null);
            Integer groupId = createGroup(user.getId());
            user.setGroupId(groupId);
            userRepository.save(user);

            TaskEntity taskEntity = buildTaskEntity(user.getId());
            taskRepository.save(taskEntity);
            String taskId = taskEntity.getId();

            TaskUpdateApiRequest updateApiRequest = buildUpdateRequest()
                    .executorIds(Set.of(user.getId()))
                    .status(taskStatus.name())
                    .build();
            //execute
            ResponseEntity<TaskApiResponse> responseEntity = client.exchange(TaskController.TASK_URL + "/" + taskId, HttpMethod.PUT, new HttpEntity<>(updateApiRequest), TaskApiResponse.class);
            //validate
            assertThat(rewardRepository.existByTaskId(taskId)).isFalse();
        }
    }

    @Test
    void WhenRewardsPointsNull_addReward() {
        //prepare
        UserEntity user = createUser(null);
        Integer groupId = createGroup(user.getId());
        user.setGroupId(groupId);
        userRepository.save(user);

        TaskEntity taskEntity = buildTaskEntity(user.getId());
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
        assertThat(rewardRepository.existByTaskId(taskId)).isFalse();
    }

    @Test
    void WhenExecutorsNull_addReward() {
        //prepare
        UserEntity user = createUser(null);
        Integer groupId = createGroup(user.getId());
        user.setGroupId(groupId);
        userRepository.save(user);

        TaskEntity taskEntity = buildTaskEntity(user.getId());
        taskRepository.save(taskEntity);
        String taskId = taskEntity.getId();

        TaskUpdateApiRequest updateApiRequest = buildUpdateRequest()
                .executorIds(Set.of())
                .status(TaskStatus.COMPLETED.name())
                .build();
        //execute
        ResponseEntity<TaskApiResponse> responseEntity = client.exchange(TaskController.TASK_URL + "/" + taskId, HttpMethod.PUT, new HttpEntity<>(updateApiRequest), TaskApiResponse.class);
        //validate
        assertThat(rewardRepository.existByTaskId(taskId)).isFalse();
    }

    private void checkFieldsForTaskResponseAndRewardEntity(TaskApiResponse taskApiResponse, RewardEntity rewardEntity) {
        assertThat(rewardEntity.getTaskId()).isEqualTo(taskApiResponse.getTaskId());
        assertThat(rewardEntity.getAmount()).isEqualTo(taskApiResponse.getRewardsPoints());
        assertThat(rewardEntity.getDescription()).isNull();
        assertThat(rewardEntity.getUpdatedBy()).isNull();
        assertThat(rewardEntity.getCreatedAt()).isNotNull();
        assertThat(rewardEntity.getUpdatedAt()).isNotNull();
    }

    private UserEntity createUser(Integer groupId) {
        UserEntity userEntity = new UserEntity();
        userEntity.setName("user name");
        userEntity.setAdmin(false);
        userEntity.setCreatedAt(LocalDateTime.now());
        userEntity.setUpdatedAt(LocalDateTime.now());
        userEntity.setGroupId(groupId);
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
                .status(TaskStatus.TO_DO.name())
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
        taskEntity.setRewardsPoints(null);

        return taskEntity;
    }

    private Integer createGroup(Integer ownerId) {
        GroupEntity groupEntity = GroupEntity.builder().ownerId(ownerId).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).deletedAt(null).build();
        groupRepository.save(groupEntity);
        return groupEntity.getId();
    }
}