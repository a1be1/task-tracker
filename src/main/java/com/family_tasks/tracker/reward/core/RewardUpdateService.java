package com.family_tasks.tracker.reward.core;

import com.family_tasks.tracker.reward.infrastructure.RewardRepository;
import com.family_tasks.tracker.reward.model.dto.RewardApiResponse;
import com.family_tasks.tracker.reward.model.dto.RewardUpdateApiRequest;
import com.family_tasks.tracker.reward.model.entity.RewardEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.family_tasks.tracker.common.error.exception.NotFoundException.rewardNotFound;

@Service
@RequiredArgsConstructor
public class RewardUpdateService {

    private final RewardRepository rewardRepository;

    public RewardApiResponse updateReward(String rewardId, RewardUpdateApiRequest patchApiRequest) {
        RewardEntity rewardEntity = rewardRepository.findById(rewardId).orElseThrow(() -> rewardNotFound(rewardId));

        rewardEntity.setAmount(patchApiRequest.getAmount());
        rewardEntity.setDescription(patchApiRequest.getDescription());
        rewardEntity.setUpdatedBy(patchApiRequest.getUserId());
        return null;
    }
}
