package com.family_tasks.tracker.user.application;

import com.family_tasks.tracker.user.core.UserCreateService;
import com.family_tasks.tracker.user.core.UserGetService;
import com.family_tasks.tracker.user.model.dto.CreateUserApiRequest;
import com.family_tasks.tracker.user.model.dto.UserApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.family_tasks.tracker.common.validation.ValidationMessage.USER_NOT_SPECIFIED;

@Validated
@RestController
@RequiredArgsConstructor
public class UserController {

    public static final String USER_URL = "/v1/users";

    private final UserCreateService createUserService;
    private final UserGetService userGetService;

    @PostMapping(USER_URL)
    public UserApiResponse createUser(@Valid @RequestBody CreateUserApiRequest request) {
        return createUserService.createUser(request);
    }

    @GetMapping(USER_URL + "/{userId}")
    public UserApiResponse getUser(@PathVariable Integer userId,
                                   @NotNull(message = USER_NOT_SPECIFIED)
                                   @RequestParam(name = "requestingUserId", required = true)
                                   Integer requestingUserId) {
        return userGetService.getUser(userId, requestingUserId);
    }
}