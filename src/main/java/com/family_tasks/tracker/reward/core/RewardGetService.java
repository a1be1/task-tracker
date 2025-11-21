package com.family_tasks.tracker.reward.core;

import com.family_tasks.tracker.reward.infrastructure.RewardRepository;
import com.family_tasks.tracker.reward.model.dto.RewardApiResponse;
import com.family_tasks.tracker.reward.model.entity.RewardEntity;
import com.family_tasks.tracker.reward.model.mapper.RewardGetMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.family_tasks.tracker.common.utils.Constants.REWARDS_PAGE_SIZE;

@Service
@RequiredArgsConstructor
public class RewardGetService {
    private final RewardRepository rewardRepository;
    private final RewardValidateService rewardValidateService;
    private final RewardGetMapper mapper;

    public List<RewardApiResponse> getRewards(Integer userId, int page) {

        rewardValidateService.validateUserExisting(userId);

        Pageable pageable = PageRequest.of(page, REWARDS_PAGE_SIZE, Sort.by("created_at").descending());
        Slice<RewardEntity> rewards = rewardRepository.findByUserId(userId, pageable);

        return rewards.stream().map(mapper::toResponse).toList();
    }
}