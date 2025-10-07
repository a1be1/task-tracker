package com.family_tasks.tracker.user.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class CreateUserApiResponse {

    @Schema(description = "The user ID")
    private final Integer userId;
    @Schema(description = "The user name")
    private final String name;
    @Schema(description = "This flag enables or disables task creation for the user.")
    private final boolean admin;
}