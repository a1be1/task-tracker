package com.family_tasks.tracker.reward.model.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class RewardAccrualRequest {
    private String taskId;
    private Integer userId;
    private Integer amount;
}