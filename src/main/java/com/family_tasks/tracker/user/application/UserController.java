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

import java.util.List;

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
    public UserApiResponse getUser(@NotNull(message = USER_NOT_SPECIFIED)
                                   @PathVariable String userId,
                                   @NotNull(message = USER_NOT_SPECIFIED)
                                   @RequestParam(name = "requestingUserId", required = false)
                                   String requestingUserId) {
        return userGetService.getUser(parseId(userId), parseId(requestingUserId));
    }

    @GetMapping(USER_URL)
    public List<UserApiResponse> getUsers(@NotNull(message = USER_NOT_SPECIFIED)
                                          @RequestParam(name = "userId", required = false)
                                          String userId) {
        return userGetService.getUsers(parseId(userId));
    }

    private Integer parseId(String stringId) {
        Integer id = null;
        try {
            id = Integer.parseInt(stringId);
        } catch (NumberFormatException exception) {
            exception.getMessage();
        }
        return id;
    }
}