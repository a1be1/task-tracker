package com.family_tasks.tracker.task.core;

import com.family_tasks.tracker.task.model.dto.TaskCreateApiRequest;
import com.family_tasks.tracker.task.model.entity.TaskEntity;
import com.family_tasks.tracker.task.model.enums.TaskPriority;
import com.family_tasks.tracker.task.model.enums.TaskStatus;
import com.family_tasks.tracker.task.model.mupper.TaskCreateMapper;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class TaskFactoryTest {
    private final TaskCreateMapper mapper = Mappers.getMapper(TaskCreateMapper.class);
    private final TaskFactory taskFactory = new TaskFactory(mapper);

    @Test
    void createTask() {
        //prepare
        TaskCreateApiRequest apiRequest = TaskCreateApiRequest.builder()
                .name("Name of task")
                .description("Description of task")
                .priority(String.valueOf(TaskPriority.HIGH))
                .reporterId(1)
                .confidential(true)
                .executorIds(Set.of(1, 2, 3, 4, 5))
                .deadline(LocalDate.now().plusDays(1))
                .build();
        //execute
        TaskEntity taskEntity = taskFactory.createTask(apiRequest);
        //validate
        assertThat(taskEntity).isNotNull();
        assertThat(taskEntity.getId()).isNotNull();
        assertThat(taskEntity.getStatus()).isEqualTo(TaskStatus.TO_DO.name());
        assertThat(taskEntity.getName()).isEqualTo(apiRequest.getName());
        assertThat(taskEntity.getDescription()).isEqualTo(apiRequest.getDescription());
        assertThat(taskEntity.getPriority()).isEqualTo(apiRequest.getPriority());
        assertThat(taskEntity.getReporterId()).isEqualTo(apiRequest.getReporterId());
        assertThat(taskEntity.getExecutorIds()).isEqualTo(apiRequest.getExecutorIds());
        assertThat(taskEntity.isConfidential()).isEqualTo(apiRequest.getConfidential());
        assertThat(taskEntity.getDeadline()).isEqualTo(apiRequest.getDeadline());
    }
}