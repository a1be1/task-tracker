package com.family_tasks.tracker.task.infrastructure;

import com.family_tasks.tracker.task.model.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, String> {
    String orderByPriority = "order by  CASE t.priority WHEN 'HIGH' THEN 3 WHEN 'MEDIUM' THEN 2 WHEN 'LOW' THEN 1 ELSE 0 END DESC;";

    @Query(value = "SELECT t.* FROM tasks t " +
            "join users u on t.reporter_id = u.id and u.group_id = :groupId " +
            "LEFT JOIN executors_tasks et ON t.id = et.task_id AND et.user_id = :userId " +
            "WHERE t.reporter_id = :userId " +
            "OR et.user_id = :userId " +
            "OR t.confidential = false " +
            orderByPriority,
            nativeQuery = true)
    List<TaskEntity> findAllTasksWithoutFilters(@Param("userId") Integer userId, @Param("groupId") Integer groupId);

    @Query(value = "SELECT t.* FROM tasks t " +
            "join users u on t.reporter_id = u.id and u.group_id = :groupId " +
            "WHERE t.status = 'CANCELLED'", nativeQuery = true)
    List<TaskEntity> findAllClosedTasks(@Param("groupId") Integer groupId);

    @Query(value = "select t.* from tasks t " +
            "where t.status !='CANCELLED' and t.status !='COMPLETED' and t.reporter_id = :userId " +
            orderByPriority,
            nativeQuery = true)
    List<TaskEntity> findTasksWhereUserIsReporterAndTasksActive(@Param("userId") Integer userId);

    @Query(value = "select t.* from tasks t " +
            "where t.status ='COMPLETED'  and t.reporter_id = :userId " +
            orderByPriority,
            nativeQuery = true)
    List<TaskEntity> findTasksWhereUserIsReporterAndTasksCompleted(@Param("userId") Integer userId);

    @Query(value = "SELECT t.* FROM tasks t " +
            "LEFT JOIN executors_tasks et ON t.id = et.task_id AND et.user_id = :userId " +
            "where et.user_id = :userId AND  t.status !='CANCELLED' and t.status !='COMPLETED' " +
            orderByPriority,
            nativeQuery = true)
    List<TaskEntity> findTasksWhereUserIsExecutorAndTasksActive(@Param("userId") Integer userId);

    @Query(value = "SELECT t.* FROM tasks t " +
            "LEFT JOIN executors_tasks et ON t.id = et.task_id AND et.user_id = :userId " +
            "where et.user_id = :userId AND  t.status = 'COMPLETED' " +
            orderByPriority,
            nativeQuery = true)
    List<TaskEntity> findTasksWhereUserIsExecutorAndTasksCompleted(@Param("userId") Integer userId);
}