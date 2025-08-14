package com.family_tasks.tracker.user.core;

import com.family_tasks.tracker.user.model.dto.CreateUserApiRequest;
import com.family_tasks.tracker.user.model.entity.UserEntity;
import com.family_tasks.tracker.user.model.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class UserFactoryTest {

    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);
    private final UserFactory userFactory = new UserFactory(mapper);

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