package com.family_tasks.tracker;

import com.family_tasks.tracker.common.utils.TimeUtils;
import com.family_tasks.tracker.group.infrastructure.GroupRepository;
import com.family_tasks.tracker.group.model.entity.GroupEntity;
import com.family_tasks.tracker.reward.model.entity.RewardEntity;
import com.family_tasks.tracker.task.model.entity.TaskEntity;
import com.family_tasks.tracker.task.model.enums.TaskPriority;
import com.family_tasks.tracker.task.model.enums.TaskStatus;
import com.family_tasks.tracker.user.infrastructure.UserRepository;
import com.family_tasks.tracker.user.model.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static com.family_tasks.tracker.utils.TestUtils.randomInt;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AbstractIntegrationTest {

    @Autowired
    protected TestRestTemplate client;
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    UserRepository userRepository;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("task-tracker")
            .withUsername("user")
            .withPassword("password");

    @DynamicPropertySource
    static void setDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    protected UserEntity createUserEntity() {
        UserEntity userEntity = new UserEntity();
        userEntity.setName("user name");
        userEntity.setAdmin(false);
        userEntity.setCreatedAt(TimeUtils.now());
        userEntity.setUpdatedAt(TimeUtils.now());
        userRepository.save(userEntity);
        return userEntity;
    }

    protected TaskEntity createTaskEntity(Integer userId) {

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

    protected Integer createGroupEntity(Integer ownerId) {
        GroupEntity groupEntity = GroupEntity.builder()
                .ownerId(ownerId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deletedAt(null)
                .build();
        groupRepository.save(groupEntity);
        return groupEntity.getId();
    }

    protected RewardEntity createRewardEntity(TaskEntity task) {
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
}