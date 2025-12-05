package com.family_tasks.tracker.reward.application;

import com.family_tasks.tracker.reward.core.RewardGetService;
import com.family_tasks.tracker.reward.core.RewardUpdateService;
import com.family_tasks.tracker.reward.model.dto.RewardApiResponse;
import com.family_tasks.tracker.reward.model.dto.RewardUpdateApiRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.*;

import static com.family_tasks.tracker.common.validation.ValidationMessage.USER_NOT_SPECIFIED;

@RestController
@RequiredArgsConstructor
@Tag(name = "Reward management", description = "Operations for managing rewards")
public class RewardController {

    public static final String REWARD_URL = "/v1/rewards";

    private final RewardGetService rewardGetService;
    private final RewardUpdateService rewardUpdateService;

    @Operation(
            summary = "Get all rewards by user ID",
            description = """
                    This endpoints returns all user's rewards. If the user
                    doesn't have any rewards, it returns an empty list.
                    """
    )

    @GetMapping(REWARD_URL)
    public Slice<RewardApiResponse> getRewards(@NotNull(message = USER_NOT_SPECIFIED)
                                               @RequestParam(name = "userId", required = false)
                                               Integer userId,
                                               Pageable page) {
        return rewardGetService.getRewards(userId, page);
    }

    @Operation(
            summary = "Update reward by reward ID",
            description = """
                    This endpoint updates an existing reward in the system.
                    
                    **Rules and constraints:**
                    - A user with "admin": false cannot edit rewards.
                    - Users without a group or belongs to a different group cannot edit rewards.
                    - The totalSum field is recalculated for all rewards starting from (and including) the modified one.
                    """
    )
    @PutMapping(REWARD_URL + "/{rewardId}")
    public RewardApiResponse updateRewardAmount(@PathVariable String rewardId,
                                                @Valid @RequestBody RewardUpdateApiRequest apiRequest) {
        return rewardUpdateService.updateReward(rewardId, apiRequest);
    }
}