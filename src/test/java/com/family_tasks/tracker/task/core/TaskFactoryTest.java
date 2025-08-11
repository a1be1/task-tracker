package com.family_tasks.tracker.task.core;

import com.family_tasks.tracker.task.model.dto.CreateTaskApiRequest;
import com.family_tasks.tracker.task.model.entity.TaskEntity;
import com.family_tasks.tracker.task.model.enums.Priority;
import com.family_tasks.tracker.task.model.enums.TaskStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class TaskFactoryTest {
    private final TaskFactory taskFactory = new TaskFactory();

    @Test
    void createTask() {
        CreateTaskApiRequest apiRequest = CreateTaskApiRequest.builder()
                .name("Name of task")
                .description("Description of task")
                .priority(String.valueOf(Priority.HIGH))
                .reporterId(UUID.randomUUID().toString())
                .executorId(UUID.randomUUID().toString())
                .confidential(true)
                .sharedWith(Set.of(UUID.randomUUID().toString(), UUID.randomUUID().toString()))
                .deadline(LocalDateTime.now())
                .build();

        TaskEntity taskEntity = taskFactory.createTask(apiRequest);
        assertThat(taskEntity).isNotNull();
        assertThat(taskEntity.getTaskId()).isNotNull();
        assertThat(taskEntity.getStatus()).isEqualTo(TaskStatus.TO_DO);
        assertThat(taskEntity.getName()).isEqualTo(apiRequest.getName());
        assertThat(taskEntity.getDescription()).isEqualTo(apiRequest.getDescription());
        assertThat(taskEntity.getPriority().name()).isEqualTo(apiRequest.getPriority());
        assertThat(taskEntity.getReporterId()).isEqualTo(apiRequest.getReporterId());
        assertThat(taskEntity.getExecutorId()).isEqualTo(apiRequest.getExecutorId());
        assertThat(taskEntity.isConfidential()).isEqualTo(apiRequest.isConfidential());
        assertThat(taskEntity.getSharedWith()).isEqualTo(apiRequest.getSharedWith());
        assertThat(taskEntity.getDeadline()).isEqualTo(apiRequest.getDeadline());
    }
}
