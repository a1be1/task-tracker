package com.family_tasks.tracker.reward.core;

import com.family_tasks.tracker.reward.model.dto.RewardAccrualRequest;
import com.family_tasks.tracker.reward.model.entity.RewardEntity;
import com.family_tasks.tracker.reward.model.mapper.RewardAccrualMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RewardFactory {

    private final RewardAccrualMapper mapper;

    public RewardEntity createReward(RewardAccrualRequest request) {

        log.info("Accruing reward. Request: {}", request);
        RewardEntity entity = mapper.toEntity(request);
        log.info("Accruing reward. Result: {}", entity);
        return entity;
    }
}