package com.family_tasks.tracker.user.core;

import com.family_tasks.tracker.user.infrastructure.UserRepository;
import com.family_tasks.tracker.user.model.dto.CreateUserApiRequest;
import com.family_tasks.tracker.user.model.dto.CreateUserApiResponse;
import com.family_tasks.tracker.user.model.entity.UserEntity;
import com.family_tasks.tracker.user.model.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateUserService {

    private final UserFactory userFactory;
    private final UserRepository userRepository;
    private final UserMapper mapper;

    public CreateUserApiResponse createUser(CreateUserApiRequest apiRequest) {
        UserEntity userEntity = userFactory.createUser(apiRequest);
        userRepository.save(userEntity);
        return mapper.toResponse(userEntity);
    }
}