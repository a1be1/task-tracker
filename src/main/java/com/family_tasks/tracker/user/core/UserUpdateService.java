package com.family_tasks.tracker.user.core;

import com.family_tasks.tracker.user.infrastructure.UserRepository;
import com.family_tasks.tracker.user.model.dto.UserApiResponse;
import com.family_tasks.tracker.user.model.dto.UserUpdateApiRequest;
import com.family_tasks.tracker.user.model.entity.UserEntity;
import com.family_tasks.tracker.user.model.mapper.UserUpdateMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.family_tasks.tracker.common.validation.ValidationMessage.USER_NOT_EXIST;

@Service
@RequiredArgsConstructor
public class UserUpdateService {

    private final UserRepository userRepository;
    private final UserUpdateMapper mapper;
    private final UserValidateService validateService;


    public UserApiResponse updateUser(Integer id, UserUpdateApiRequest request) {
        validateService.validateUserExisting(id);
        validateService.validateGroupExisting(request.getGroupId());

        Optional<UserEntity> fromDB = userRepository.findById(id);

        return fromDB.map(user -> {
            mapper.fillWithRequest(user, request);
            userRepository.save(user);
            return mapper.toResponse(user);
        }).orElseThrow(() -> new IllegalArgumentException(String.format(USER_NOT_EXIST, id)));
    }
}