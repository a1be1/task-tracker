package com.family_tasks.tracker.user.application;

import com.family_tasks.tracker.user.core.UserCreateService;
import com.family_tasks.tracker.user.core.UserGetService;
import com.family_tasks.tracker.user.core.UserUpdateService;
import com.family_tasks.tracker.user.model.dto.UserApiResponse;
import com.family_tasks.tracker.user.model.dto.UserCreateApiRequest;
import com.family_tasks.tracker.user.model.dto.UserUpdateApiRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.family_tasks.tracker.common.utils.ValidateUtils.parseId;
import static com.family_tasks.tracker.common.validation.ValidationMessage.GROUP_NOT_SPECIFIED;
import static com.family_tasks.tracker.common.validation.ValidationMessage.USER_NOT_SPECIFIED;

@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "User management", description = "Operations for managing users")
public class UserController {

    public static final String USER_URL = "/v1/users";

    private final UserCreateService userCreateService;
    private final UserGetService userGetService;
    private final UserUpdateService userUpdateService;

    @Operation(
            summary = "Create a new user",
            description = "This endpoint is assigned to a new user in the system. " +
                    "The groupId can be entered during setup if user is registered via an invitation link."
    )
    @PostMapping(USER_URL)
    public UserApiResponse createUser(@Valid @RequestBody UserCreateApiRequest request) {
        return userCreateService.createUser(request);
    }

    @Operation(
            summary = "Update an existing user",
            description = "This endpoint updates the user in the system."
    )
    @PutMapping(USER_URL + "/{userId}")
    public UserApiResponse updateUser(@NotNull(message = USER_NOT_SPECIFIED)
                                      @PathVariable String userId,
                                      @Valid @RequestBody UserUpdateApiRequest request) {
        return userUpdateService.updateUser(parseId(userId), request);
    }

    @Operation(
            summary = "Get an existing user",
            description = "This endpoint retrieves a user by its unique ID."
    )
    @GetMapping(USER_URL + "/{userId}")
    public UserApiResponse getUser(@NotNull(message = USER_NOT_SPECIFIED)
                                   @PathVariable String userId) {
        return userGetService.getUser(parseId(userId));
    }

    @Operation(
            summary = "Get all users from group",
            description = "This endpoint retrieves all users from a specific group"
    )
    @GetMapping(USER_URL)
    public List<UserApiResponse> getUsers(@NotNull(message = GROUP_NOT_SPECIFIED)
                                          @RequestParam(name = "groupId", required = false)
                                          String groupId) {
        return userGetService.getUsers(parseId(groupId));
    }
}