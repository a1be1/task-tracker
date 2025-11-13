package com.family_tasks.tracker.reward.application;

import com.family_tasks.tracker.AbstractIntegrationTest;
import com.family_tasks.tracker.common.error.ErrorResponse;
import com.family_tasks.tracker.reward.infrastructure.RewardRepository;
import com.family_tasks.tracker.reward.model.dto.RewardApiResponse;
import com.family_tasks.tracker.reward.model.entity.RewardEntity;
import com.family_tasks.tracker.task.infrastructure.TaskRepository;
import com.family_tasks.tracker.task.model.entity.TaskEntity;
import com.family_tasks.tracker.user.model.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static com.family_tasks.tracker.common.validation.ValidationMessage.USER_NOT_EXIST;
import static com.family_tasks.tracker.common.validation.ValidationMessage.VALIDATION_FAILED;
import static org.assertj.core.api.Assertions.assertThat;

public class RewardGetControllerTest extends AbstractIntegrationTest {
    @Autowired
    TaskRepository taskRepository;
    @Autowired
    RewardRepository rewardRepository;

    @Test
    void whenRewardExist_getAllRewards() {
        //prepare
        UserEntity user = createUserEntity();

        TaskEntity taskEntity = createTaskEntity(user.getId());
        taskRepository.save(taskEntity);

        RewardEntity rewardEntity = createRewardEntity(taskEntity);
        rewardRepository.save(rewardEntity);

        String url = UriComponentsBuilder
                .fromUriString(RewardController.REWARD_URL)
                .queryParam("userId", user.getId())
                .toUriString();
        //execute
        ResponseEntity<List<RewardApiResponse>> responseEntity =
                client.exchange(
                        url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<RewardApiResponse>>() {
                        }
                );
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<RewardApiResponse> responses = responseEntity.getBody();
        assert responses != null;
        assertThat(responses).containsExactlyInAnyOrderElementsOf(List.of(toApiResponse(rewardEntity)));
    }

    @Test
    void whenTwoRewards_getAllRewards() {
        //prepare
        UserEntity user = createUserEntity();

        TaskEntity taskEntity1 = createTaskEntity(user.getId());
        taskRepository.save(taskEntity1);

        TaskEntity taskEntity2 = createTaskEntity(user.getId());
        taskRepository.save(taskEntity2);

        RewardEntity rewardEntity1 = createRewardEntity(taskEntity2);
        rewardRepository.save(rewardEntity1);

        RewardEntity rewardEntity2 = createRewardEntity(taskEntity2);
        rewardEntity2.setTotalSum(taskEntity1.getRewardsPoints() + taskEntity2.getRewardsPoints());
        rewardRepository.save(rewardEntity2);

        String url = UriComponentsBuilder
                .fromUriString(RewardController.REWARD_URL)
                .queryParam("userId", user.getId())
                .toUriString();
        //execute
        ResponseEntity<List<RewardApiResponse>> responseEntity =
                client.exchange(
                        url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<RewardApiResponse>>() {
                        }
                );
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<RewardApiResponse> responses = responseEntity.getBody();
        assert responses != null;
        assertThat(responses).containsExactlyInAnyOrderElementsOf(List.of(toApiResponse(rewardEntity1), toApiResponse(rewardEntity2)));
    }

    @Test
    void whenForeignRewardExist_getAllRewards() {
        //prepare
        UserEntity user = createUserEntity();

        TaskEntity taskEntity = createTaskEntity(user.getId());
        taskRepository.save(taskEntity);

        RewardEntity rewardEntity = createRewardEntity(taskEntity);
        rewardRepository.save(rewardEntity);

        UserEntity userWithoutReward = createUserEntity();

        String url = UriComponentsBuilder
                .fromUriString(RewardController.REWARD_URL)
                .queryParam("userId", userWithoutReward.getId())
                .toUriString();
        //execute
        ResponseEntity<List<RewardApiResponse>> responseEntity =
                client.exchange(
                        url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<RewardApiResponse>>() {
                        }
                );
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<RewardApiResponse> responses = responseEntity.getBody();
        assertThat(responses).isEmpty();
    }

    @Test
    void whenRewardNotExist_getAllRewards() {
        //prepare
        UserEntity user = createUserEntity();

        String url = UriComponentsBuilder
                .fromUriString(RewardController.REWARD_URL)
                .queryParam("userId", user.getId())
                .toUriString();
        //execute
        ResponseEntity<List<RewardApiResponse>> responseEntity =
                client.exchange(
                        url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<RewardApiResponse>>() {
                        }
                );
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<RewardApiResponse> responses = responseEntity.getBody();
        assertThat(responses).isEmpty();
    }

    @Test
    void whenUserNotExist_getAllRewards() {
        //prepare
        Integer notExistUserId = createUserEntity().getId() + 2;

        String url = UriComponentsBuilder
                .fromUriString(RewardController.REWARD_URL)
                .queryParam("userId", notExistUserId)
                .toUriString();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.getForEntity(url, ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(String.format(USER_NOT_EXIST, notExistUserId));
    }

    @Test
    void whenUserNull_getAllRewards() {
        //prepare
        Integer notExistUserId = null;

        String url = UriComponentsBuilder
                .fromUriString(RewardController.REWARD_URL)
                .queryParam("userId", notExistUserId)
                .toUriString();
        //execute
        ResponseEntity<ErrorResponse> responseEntity = client.getForEntity(url, ErrorResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.errorMessage()).isEqualTo(VALIDATION_FAILED);
    }

    private RewardApiResponse toApiResponse(RewardEntity rewardEntity) {
        return RewardApiResponse.builder()
                .rewardId(rewardEntity.getId())
                .taskId(rewardEntity.getTaskId())
                .userId(rewardEntity.getUserId())
                .amount(rewardEntity.getAmount())
                .description(rewardEntity.getDescription())
                .createdAt(rewardEntity.getCreatedAt())
                .updatedAt(rewardEntity.getUpdatedAt())
                .totalSum(rewardEntity.getTotalSum())
                .updatedBy(rewardEntity.getUpdatedBy())
                .build();
    }
}