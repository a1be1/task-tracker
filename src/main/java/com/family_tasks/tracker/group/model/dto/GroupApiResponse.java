package com.family_tasks.tracker.group.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class GroupApiResponse {
    @Schema(description = "The group ID")
    private final Integer groupId;
    @Schema(description = "The owner ID")
    private final Integer ownerId;
    @Schema(description = "The datetime of the group creation.")
    private final LocalDateTime createdAt;
    @Schema(description = "The datetime of the last group update.")
    private final LocalDateTime updatedAt;
    @Schema(description = "The datetime of the group deletion.")
    private final LocalDateTime deletedAt;
}