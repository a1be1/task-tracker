package com.family_tasks.tracker.reward.model.entity;

import com.family_tasks.tracker.common.TableNames;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity(name = TableNames.REWARDS)
@Setter
@Getter
@ToString
@NoArgsConstructor
public class RewardEntity {
    @Id
    private String id;
    private String taskId;
    private Integer userId;
    private Integer updatedBy;
    private Integer amount;
    private Integer totalSum;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}