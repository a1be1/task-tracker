package com.family_tasks.tracker.task.infrastructure;

import com.family_tasks.tracker.task.model.entity.TaskEntity;

//TODO: implement JPA
public interface TaskRepository {

    void saveTask(TaskEntity task);

    TaskEntity getTask(String taskId);
}
