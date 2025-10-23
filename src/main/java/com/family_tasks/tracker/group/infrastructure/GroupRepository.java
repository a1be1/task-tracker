package com.family_tasks.tracker.group.infrastructure;

import com.family_tasks.tracker.group.model.entity.GroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<GroupEntity, Integer> {

    @Query(value = "SELECT EXISTS (SELECT 1 FROM groups g WHERE g.owner_id = :ownerId)", nativeQuery = true)
    boolean existsByOwnerId(@Param("ownerId") Integer ownerId);

    @Query(value = "SELECT EXISTS (SELECT 1 FROM groups g WHERE g.id = :groupId)", nativeQuery = true)
    boolean existByGroupId(@Param("groupId") Integer groupId);
}