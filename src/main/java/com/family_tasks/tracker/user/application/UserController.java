package com.family_tasks.tracker.user.application;

import com.family_tasks.tracker.user.core.CreateUserService;
import com.family_tasks.tracker.user.model.dto.CreateUserApiRequest;
import com.family_tasks.tracker.user.model.dto.CreateUserApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    public static final String USER_URL = "/v1/users";

    private final CreateUserService createUserService;

    //TODO: implement validations
    @PostMapping(USER_URL)
    public CreateUserApiResponse createUser(@RequestBody CreateUserApiRequest request) {
        return createUserService.createUser(request);
    }
}