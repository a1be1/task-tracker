package com.family_tasks.tracker.reward.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import static com.family_tasks.tracker.common.validation.ValidationConstants.REWARD_DESCRIPTION_MAX_LENGTH;
import static com.family_tasks.tracker.common.validation.ValidationConstants.REWARD_DESCRIPTION_MIN_LENGTH;
import static com.family_tasks.tracker.common.validation.ValidationMessage.*;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class RewardUpdateApiRequest {
    @PositiveOrZero(message = REWARDS_POINTS_POSITIVE)
    @NotNull(message = REWARD_AMOUNT_NULL)
    private Integer amount;
    @NotNull(message = USER_NOT_SPECIFIED)
    private Integer updatedBy;
    @Size(max = REWARD_DESCRIPTION_MAX_LENGTH, message = REWARD_DESCRIPTION_TOO_LONG)
    @Size(min = REWARD_DESCRIPTION_MIN_LENGTH, message = REWARD_DESCRIPTION_TOO_SHORT)
    @NotNull(message = REWARD_DESCRIPTION_NULL)
    private String description;
}
