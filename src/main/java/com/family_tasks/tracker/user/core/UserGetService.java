package com.family_tasks.tracker.user.core;

import com.family_tasks.tracker.common.error.exception.NotFoundException;
import com.family_tasks.tracker.user.infrastructure.UserRepository;
import com.family_tasks.tracker.user.model.dto.UserApiResponse;
import com.family_tasks.tracker.user.model.entity.UserEntity;
import com.family_tasks.tracker.user.model.mapper.UserGetMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserGetService {

    private final UserRepository userRepository;
    private final UserGetMapper mapper;
    private final UserValidateService validateService;

    public UserApiResponse getUser(Integer userId, Integer requestingUserId) {

        validateService.validateUserExisting(requestingUserId);

        Optional<UserEntity> fromDB = userRepository.findById(userId);
        UserEntity userEntity = fromDB.orElseThrow(() -> NotFoundException.userNotFound(userId));

        return mapper.toResponse(userEntity);
    }
}
