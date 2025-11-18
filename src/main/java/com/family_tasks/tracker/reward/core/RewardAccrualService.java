package com.family_tasks.tracker.reward.core;

import com.family_tasks.tracker.reward.infrastructure.RewardRepository;
import com.family_tasks.tracker.reward.model.dto.RewardAccrualRequest;
import com.family_tasks.tracker.reward.model.entity.RewardEntity;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RewardAccrualService {

    private final RewardRepository rewardRepository;
    private final RewardFactory rewardFactory;

    @Transactional
    public void accrualReward(RewardAccrualRequest request) {
        RewardEntity last = rewardRepository.findLastByUserIdOrderByCreatedAtDesc(request.getUserId()).orElse(null);
        int newTotal = (last != null ? last.getTotalSum() : 0) + request.getAmount();

        RewardEntity rewardEntity = rewardFactory.createReward(request);
        rewardEntity.setTotalSum(newTotal);

        rewardRepository.save(rewardEntity);
    }
}