package com.family_tasks.tracker.reward.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class RewardUpdateApiRequest {
    @NotNull
    private Integer amount;
    @NotNull
    private Integer userId;
    @NotNull
    private String description;
}
