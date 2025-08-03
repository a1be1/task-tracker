package com.family_tasks.tracker.user.infrastructure;

import com.family_tasks.tracker.user.model.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.HashMap;

@Slf4j
@Repository
public class UserHashMapRepository implements UserRepository {

    private final HashMap<String, UserEntity> userTempStorage = new HashMap<>();

    @Override
    public void saveUser(UserEntity user) {
        userTempStorage.put(user.getId(), user);
    }

    public UserEntity getUser(String userId) {
        if (!userTempStorage.containsKey(userId)) {
            log.error("User with id {} wasn't found!", userId);
            throw new RuntimeException("User not found!");
        }
        return userTempStorage.get(userId);
    }
}
