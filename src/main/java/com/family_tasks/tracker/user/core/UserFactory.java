package com.family_tasks.tracker.user.core;

import com.family_tasks.tracker.user.model.dto.CreateUserApiRequest;
import com.family_tasks.tracker.user.model.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Component
public class UserFactory {

    public UserEntity createUser(CreateUserApiRequest request) {
        log.info("Creating a new user. Request: {}", request);
        //TODO: consider generating ID by DB
        String id = UUID.randomUUID().toString();
        UserEntity entity = UserEntity.builder()
                .id(id)
                .name(request.getName())
                .admin(Boolean.TRUE == request.getAdmin())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        log.info("Creating a new user. Result: {}", entity);
        return entity;
    }
}
