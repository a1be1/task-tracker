package com.family_tasks.tracker.user.infrastructure;

import com.family_tasks.tracker.user.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    @Query(value = "SELECT u.* from users u WHERE u.group_id = :groupId", nativeQuery = true)
    List<UserEntity> findAllUsersByGroup(@Param("groupId") Integer groupId);
}