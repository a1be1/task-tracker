package com.family_tasks.tracker.user.core;

import com.family_tasks.tracker.user.model.dto.CreateUserApiRequest;
import com.family_tasks.tracker.user.model.entity.UserEntity;
import com.family_tasks.tracker.user.model.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserFactory {

    private final UserMapper mapper;

    public UserEntity createUser(CreateUserApiRequest request) {
        log.info("Creating a new user. Request: {}", request);
        //TODO: consider generating ID by DB
        UserEntity entity = mapper.toEntity(request);
        log.info("Creating a new user. Result: {}", entity);
        return entity;
    }
}