package com.family_tasks.tracker.user.core;

import com.family_tasks.tracker.user.infrastructure.UserRepository;
import com.family_tasks.tracker.user.model.dto.CreateUserApiRequest;
import com.family_tasks.tracker.user.model.dto.CreateUserApiResponse;
import com.family_tasks.tracker.user.model.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateUserService {

    private final UserFactory userFactory;
    private final UserRepository userRepository;

    public CreateUserApiResponse createUser(CreateUserApiRequest apiRequest) {
        UserEntity userEntity = userFactory.createUser(apiRequest);
        userRepository.saveUser(userEntity);
        return toResponse(userEntity);
    }

    private CreateUserApiResponse toResponse(UserEntity userEntity) {
        return CreateUserApiResponse.builder()
                .userId(userEntity.getId())
                .name(userEntity.getName())
                .admin(userEntity.isAdmin())
                .build();
    }
}