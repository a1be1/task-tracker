package com.family_tasks.tracker.reward.infrastructure;

import com.family_tasks.tracker.reward.model.entity.RewardEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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
            LIMIT 1
            FOR UPDATE;
            """, nativeQuery = true)
    Optional<RewardEntity> findLastByUserIdOrderByCreatedAtDesc(@Param("userId") Integer userId);

    @Query(value = "SELECT EXISTS (SELECT 1 FROM rewards r WHERE r.task_id = :taskId AND r.user_id = :userId)", nativeQuery = true)
    Boolean existByTaskIdAndUserId(@Param("taskId") String taskId, @Param("userId") Integer userId);

    @Query(value = "SELECT r.* FROM rewards r WHERE r.task_id = :taskId", nativeQuery = true)
    List<RewardEntity> findByTaskId(@Param("taskId") String taskId);

    @Query(value = """
                SELECT r.* FROM rewards r
                    WHERE user_id = :userId
            """, nativeQuery = true)
    Slice<RewardEntity> findByUserId(@Param("userId") Integer userId, Pageable pageable);

    @Query(value = """
                SELECT r.* FROM rewards r
                    WHERE r.user_id = :userId
                    AND r.created_at  >= :createdAt
                    ORDER BY r.created_at ASC
                    FOR UPDATE;
            """, nativeQuery = true)
    List<RewardEntity> findAllByUserIdAndCreatedAtAfterOrEqual(
            @Param("userId") Integer userId,
            @Param("createdAt") LocalDateTime createdAt);
}