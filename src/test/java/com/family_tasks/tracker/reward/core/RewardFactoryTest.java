package com.family_tasks.tracker.reward.core;

import com.family_tasks.tracker.reward.model.dto.RewardAccrualRequest;
import com.family_tasks.tracker.reward.model.entity.RewardEntity;
import com.family_tasks.tracker.reward.model.mapper.RewardAccrualMapper;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class RewardFactoryTest {
    private final RewardAccrualMapper mapper = Mappers.getMapper(RewardAccrualMapper.class);
    private final RewardFactory rewardFactory = new RewardFactory(mapper);

    @Test
    void createReward() {
        //prepare
        RewardAccrualRequest request = RewardAccrualRequest.builder()
                .userId(1)
                .amount(1)
                .build();
        //execute
        RewardEntity rewardEntity = rewardFactory.createReward(request);
        //validate
        assertThat(rewardEntity).isNotNull();
        assertThat(rewardEntity.getId()).isNotNull();
        assertThat(rewardEntity.getCreatedAt()).isNotNull();
        assertThat(rewardEntity.getUpdatedAt()).isNotNull();
        assertThat(rewardEntity.getDescription()).isNull();
        assertThat(rewardEntity.getUpdatedBy()).isNull();
        assertThat(rewardEntity.getTaskId()).isEqualTo(request.getTaskId());
        assertThat(rewardEntity.getUserId()).isEqualTo(request.getUserId());
    }
}