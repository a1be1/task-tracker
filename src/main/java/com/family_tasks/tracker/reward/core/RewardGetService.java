package com.family_tasks.tracker.reward.core;

import com.family_tasks.tracker.reward.infrastructure.RewardRepository;
import com.family_tasks.tracker.reward.model.dto.RewardApiResponse;
import com.family_tasks.tracker.reward.model.entity.RewardEntity;
import com.family_tasks.tracker.reward.model.mapper.RewardGetMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RewardGetService {
    private final RewardRepository rewardRepository;
    private final RewardValidateService rewardValidateService;
    private final RewardGetMapper mapper;

    public List<RewardApiResponse> getRewards(Integer userId) {

        rewardValidateService.validateUserExisting(userId);
        List<RewardEntity> rewards = rewardRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return rewards.stream()
                .map(mapper::toResponse)
                .toList();
    }
}