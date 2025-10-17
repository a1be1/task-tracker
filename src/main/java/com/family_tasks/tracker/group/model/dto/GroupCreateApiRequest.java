package com.family_tasks.tracker.group.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import static com.family_tasks.tracker.common.validation.ValidationMessage.GROUP_OWNER_NOT_SPECIFIED;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class GroupCreateApiRequest {
    @Schema(description = "The owner id. An owner can have only one group.")
    @NotNull(message = GROUP_OWNER_NOT_SPECIFIED)
    Integer ownerId;
}