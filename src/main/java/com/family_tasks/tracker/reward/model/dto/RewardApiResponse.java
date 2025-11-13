package com.family_tasks.tracker.reward.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class RewardApiResponse {
    @Schema(description = "The reward ID.")
    private String rewardId;
    @Schema(description = "The ID of the task for which the reward was accrued.")
    private String taskId;
    @Schema(description = "The ID of the user who received the reward.")
    private Integer userId;
    @Schema(description = "The ID of the user who made the updating.")
    private Integer updatedBy;
    @Schema(description = "The number of points earned for the task.")
    private Integer amount;
    @Schema(description = "The total number of points earned for the tasks.")
    private Integer totalSum;
    @Schema(description = "Comments on the reward. This is applicable only when updating reward.")
    private String description;
    @Schema(description = "The datetime of the reward accrual.")
    private LocalDateTime createdAt;
    @Schema(description = "The datetime of the last task update.")
    private LocalDateTime updatedAt;
}