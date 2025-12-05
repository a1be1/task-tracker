package com.family_tasks.tracker.reward.core;

import com.family_tasks.tracker.reward.infrastructure.RewardRepository;
import com.family_tasks.tracker.reward.model.dto.RewardApiResponse;
import com.family_tasks.tracker.reward.model.entity.RewardEntity;
import com.family_tasks.tracker.reward.model.mapper.RewardGetMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RewardGetService {
    private final RewardRepository rewardRepository;
    private final RewardValidateService rewardValidateService;
    private final RewardGetMapper mapper;

    public Slice<RewardApiResponse> getRewards(Integer userId, Pageable page) {

        rewardValidateService.validateUserExisting(userId);

        Slice<RewardEntity> rewards = rewardRepository.findByUserId(userId, page);

        return rewards.map(mapper::toResponse);
    }
}