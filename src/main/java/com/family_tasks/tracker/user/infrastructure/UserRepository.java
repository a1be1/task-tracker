package com.family_tasks.tracker.user.infrastructure;

import com.family_tasks.tracker.user.model.entity.UserEntity;

//TODO: implement JPA
public interface UserRepository {

    void saveUser(UserEntity user);

    UserEntity getUser(String userId);
}
