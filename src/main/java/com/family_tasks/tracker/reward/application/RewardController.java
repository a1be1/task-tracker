package com.family_tasks.tracker.reward.application;

import com.family_tasks.tracker.reward.core.RewardGetService;
import com.family_tasks.tracker.reward.core.RewardPatchService;
import com.family_tasks.tracker.reward.model.dto.RewardApiResponse;
import com.family_tasks.tracker.reward.model.dto.RewardUpdateApiRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.family_tasks.tracker.common.validation.ValidationMessage.USER_NOT_SPECIFIED;

@RestController
@RequiredArgsConstructor
@Tag(name = "Reward management", description = "Operations for managing rewards")
public class RewardController {

    public static final String REWARD_URL = "/v1/rewards";

    private final RewardGetService rewardGetService;
    private final RewardPatchService rewardPatchService;

    @Operation(
            summary = "Get all rewards by user ID",
            description = """
                    This endpoints returns all user's rewards. If the user
                    doesn't have any rewards, it returns an empty list.
                    """
    )
    @GetMapping(REWARD_URL)
    public List<RewardApiResponse> getRewards(@NotNull(message = USER_NOT_SPECIFIED)
                                              @RequestParam(name = "userId", required = false)
                                              Integer userId) {
        return rewardGetService.getRewards(userId);
    }

    @PatchMapping(REWARD_URL + "/{rewardId}")
    public RewardApiResponse updateRewardAmount(@PathVariable String rewardId,
                                                @Valid @RequestBody RewardUpdateApiRequest patchRequest) {
        return rewardPatchService.patchReward(rewardId, patchRequest);
    }
}