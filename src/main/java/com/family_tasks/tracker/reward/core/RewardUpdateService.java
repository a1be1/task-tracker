package com.family_tasks.tracker.reward.core;

import com.family_tasks.tracker.common.utils.TimeUtils;
import com.family_tasks.tracker.reward.infrastructure.RewardRepository;
import com.family_tasks.tracker.reward.model.dto.RewardApiResponse;
import com.family_tasks.tracker.reward.model.dto.RewardUpdateApiRequest;
import com.family_tasks.tracker.reward.model.entity.RewardEntity;
import com.family_tasks.tracker.reward.model.mapper.RewardUpdateMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.family_tasks.tracker.common.error.exception.NotFoundException.rewardNotFound;

@Service
@RequiredArgsConstructor
public class RewardUpdateService {

    private final RewardRepository rewardRepository;
    private final RewardUpdateMapper mapper;
    private final RewardValidateService validateService;

    public RewardApiResponse updateReward(String rewardId, RewardUpdateApiRequest updateApiRequest) {
        RewardEntity rewardEntity = rewardRepository.findById(rewardId).orElseThrow(() -> rewardNotFound(rewardId));

        validateService.validateRewardUpdating(updateApiRequest.getUpdatedBy(), rewardEntity.getUserId());

        int totalSumDelta = updateApiRequest.getAmount() - rewardEntity.getAmount();

        mapper.fillWithRequest(rewardEntity, updateApiRequest);
        rewardRepository.save(rewardEntity);

        changeTotalSumFrom(totalSumDelta, rewardEntity);

        return mapper.toResponse(rewardEntity);
    }

    private void changeTotalSumFrom(int totalSumDelta, RewardEntity startRewardEntity) {
        Integer userId = startRewardEntity.getUserId();
        LocalDateTime startDate = startRewardEntity.getCreatedAt();

        List<RewardEntity> rewards = rewardRepository.findAllByUserIdAndCreatedAtAfterOrEqual(userId, startDate);

        rewards.forEach(r -> {
            r.setTotalSum(Math.max(0, r.getTotalSum() + totalSumDelta));
            r.setUpdatedAt(TimeUtils.now());
        });

        rewardRepository.saveAll(rewards);
    }
}
