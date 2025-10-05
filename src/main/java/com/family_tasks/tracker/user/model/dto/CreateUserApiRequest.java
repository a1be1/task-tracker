package com.family_tasks.tracker.user.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import static com.family_tasks.tracker.common.validation.ValidationConstants.USER_NAME_MAX_LENGTH;
import static com.family_tasks.tracker.common.validation.ValidationMessage.IS_ADMIN_NOT_SPECIFIED;
import static com.family_tasks.tracker.common.validation.ValidationMessage.USER_NAME_NOT_SPECIFIED;
import static com.family_tasks.tracker.common.validation.ValidationMessage.USER_NAME_TOO_LONG;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class CreateUserApiRequest {

    @Schema(description = "The user name")
    @Size(max = USER_NAME_MAX_LENGTH, message = USER_NAME_TOO_LONG)
    @NotEmpty(message = USER_NAME_NOT_SPECIFIED)
    private final String name;
    @Schema(description = "This flag enables or disables task creation for the user.")
    @NotNull(message = IS_ADMIN_NOT_SPECIFIED)
    private final Boolean admin;
}
