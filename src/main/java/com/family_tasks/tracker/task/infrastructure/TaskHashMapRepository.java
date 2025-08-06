package com.family_tasks.tracker.task.infrastructure;

import com.family_tasks.tracker.task.model.entity.TaskEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.HashMap;

@Slf4j
@Repository
public class TaskHashMapRepository implements TaskRepository {

    private final HashMap<String, TaskEntity> taskTempStorage = new HashMap<>();

    @Override
    public void saveTask(TaskEntity task) {
        taskTempStorage.put(task.getTaskId(), task);
    }

    @Override
    public TaskEntity getTask(String taskId) {
        if (!taskTempStorage.containsKey(taskId)) {
            log.error("Task with id {} wasn't found!", taskId);
            throw new RuntimeException("Task not found!");
        }
        return taskTempStorage.get(taskId);
    }
}