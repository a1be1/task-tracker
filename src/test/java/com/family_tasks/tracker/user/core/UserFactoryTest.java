package com.family_tasks.tracker.user.core;

import com.family_tasks.tracker.user.model.dto.CreateUserApiRequest;
import com.family_tasks.tracker.user.model.entity.UserEntity;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class UserFactoryTest {

    private final UserFactory userFactory = new UserFactory();

    @Test
    void createUser() {
        CreateUserApiRequest apiRequest = CreateUserApiRequest.builder()
                .name("Some user name")
                .admin(true)
                .build();

        UserEntity userEntity = userFactory.createUser(apiRequest);
        assertThat(userEntity.getId()).isNotNull();
        assertThat(userEntity.getName()).isEqualTo(apiRequest.getName());
        assertThat(userEntity.isAdmin()).isTrue();
        assertThat(userEntity.getCreatedAt()).isNotNull();
        assertThat(userEntity.getUpdatedAt()).isNotNull();
    }
}