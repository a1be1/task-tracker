package com.family_tasks.tracker.reward.infrastructure;

import com.family_tasks.tracker.reward.model.entity.RewardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RewardRepository extends JpaRepository<RewardEntity, String> {

    @Query(value = """
                SELECT r.* FROM rewards r
                    WHERE user_id = :userId
                    ORDER BY created_at DESC
                    LIMIT 1;
            """, nativeQuery = true)
    Optional<RewardEntity> findLastByUserIdOrderByCreatedAtDesc(@Param("userId") Integer userId);

    @Query(value = "SELECT EXISTS (SELECT 1 FROM rewards r WHERE r.task_id = :taskId)", nativeQuery = true)
    Boolean existByTaskId(@Param("taskId") String taskId);

    @Query(value = "SELECT r.* FROM rewards r WHERE r.task_id = :taskId", nativeQuery = true)
    List<RewardEntity> findByTaskId(@Param("taskId") String taskId);

    @Query(value = """
                SELECT r.* FROM rewards r
                    WHERE user_id = :userId
                    ORDER BY created_at DESC
            """, nativeQuery = true)
    List<RewardEntity> findByUserIdOrderByCreatedAtDesc(@Param("userId") Integer userId);

    @Query(value = """
                SELECT r.* FROM rewards r
                    WHERE r.user_id = :userId
                    AND r.created_at  >= :createdAt
                    ORDER BY r.created_at ASC
            """, nativeQuery = true)
    List<RewardEntity> findAllByUserIdAndCreatedAtAfterOrEqual(
            @Param("userId") Integer userId,
            @Param("createdAt") LocalDateTime createdAt);
}