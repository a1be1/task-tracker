package com.family_tasks.tracker.user.core;

import com.family_tasks.tracker.user.infrastructure.UserRepository;
import com.family_tasks.tracker.user.model.dto.UserCreateApiRequest;
import com.family_tasks.tracker.user.model.dto.UserApiResponse;
import com.family_tasks.tracker.user.model.entity.UserEntity;
import com.family_tasks.tracker.user.model.mapper.UserCreateMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCreateService {

    private final UserFactory userFactory;
    private final UserRepository userRepository;
    private final UserCreateMapper mapper;
    private final UserValidateService validateService;

    public UserApiResponse createUser(UserCreateApiRequest apiRequest) {
        validateService.validateUserCreation(apiRequest);
        UserEntity userEntity = userFactory.createUser(apiRequest);
        userRepository.save(userEntity);
        return mapper.toResponse(userEntity);
    }
}