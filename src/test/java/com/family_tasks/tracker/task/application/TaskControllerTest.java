package com.family_tasks.tracker.task.application;

import com.family_tasks.tracker.AbstractApplicationTest;
import com.family_tasks.tracker.task.infrastructure.TaskRepository;
import com.family_tasks.tracker.task.model.dto.CreateTaskApiRequest;
import com.family_tasks.tracker.task.model.dto.CreateTaskApiResponse;
import com.family_tasks.tracker.task.model.dto.UpdateTaskApiRequest;
import com.family_tasks.tracker.task.model.dto.UpdateTaskApiResponse;
import com.family_tasks.tracker.task.model.entity.TaskEntity;
import com.family_tasks.tracker.task.model.enums.Priority;
import com.family_tasks.tracker.task.model.enums.TaskStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static com.family_tasks.tracker.task.application.TaskController.TASK_URL;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link TaskController}
 */
public class TaskControllerTest extends AbstractApplicationTest {

    @Autowired
    TaskRepository taskRepository;

    @Test
    void createTask() {
        //prepare
        CreateTaskApiRequest request = buildCreateRequest();
        //execute
        ResponseEntity<CreateTaskApiResponse> responseEntity = client.postForEntity(TASK_URL, request, CreateTaskApiResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        CreateTaskApiResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.getTaskId()).isNotNull();
        assertThat(response.getStatus()).isEqualTo(TaskStatus.TO_DO);
        assertThat(response.getName()).isEqualTo(request.getName());
        assertThat(response.getDescription()).isEqualTo(request.getDescription());
        assertThat(response.getPriority().name()).isEqualTo(request.getPriority());
        assertThat(response.getReporterId()).isEqualTo(request.getReporterId());
        assertThat(response.getExecutorId()).isEqualTo(request.getExecutorId());
        assertThat(response.isConfidential()).isEqualTo(request.isConfidential());
        assertThat(response.getSharedWith()).isEqualTo(request.getSharedWith());
        assertThat(response.getDeadline()).isEqualTo(request.getDeadline());

        TaskEntity taskEntity = taskRepository.getTask(response.getTaskId());
        assertThat(taskEntity).isNotNull();
    }

    @Test
    void updateTaskWhenTaskExist() {
        //prepare
        TaskEntity taskEntity = buildTaskEntity();
        taskRepository.saveTask(taskEntity);
        String taskId = taskEntity.getTaskId();
        UpdateTaskApiRequest request = buildUpdateRequest();
        //execute
        ResponseEntity<UpdateTaskApiResponse> responseEntity = client.exchange(TaskController.TASK_URL + "/" + taskId, HttpMethod.PUT, new HttpEntity<>(request), UpdateTaskApiResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        UpdateTaskApiResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(TaskStatus.TO_DO);
        assertThat(response.getName()).isEqualTo(request.getName());
        assertThat(response.getDescription()).isEqualTo(request.getDescription());
        assertThat(response.getPriority().name()).isEqualTo(request.getPriority());
        assertThat(response.getExecutorId()).isEqualTo(request.getExecutorId());
        assertThat(response.isConfidential()).isEqualTo(request.isConfidential());
        assertThat(response.getSharedWith()).isEqualTo(request.getSharedWith());
        assertThat(response.getDeadline()).isEqualTo(request.getDeadline());
    }

    @Test
    void updateTaskWhenTAskNotExist() {
        //prepare
        String taskId = UUID.randomUUID().toString();
        UpdateTaskApiRequest request = buildUpdateRequest();
        //execute
        var responseEntity = client.exchange(TaskController.TASK_URL + "/" + taskId, HttpMethod.PUT, new HttpEntity<>(request), String.class);
        //validate
        //TODO write this test
    }

    private CreateTaskApiRequest buildCreateRequest() {
        return CreateTaskApiRequest.builder()
                .name("Name of task")
                .description("Description of task")
                .priority(Priority.HIGH.name())
                .reporterId(UUID.randomUUID().toString())
                .executorId(UUID.randomUUID().toString())
                .confidential(true)
                .sharedWith(Set.of(UUID.randomUUID().toString(), UUID.randomUUID().toString()))
                .deadline(LocalDateTime.now())
                .build();
    }

    private UpdateTaskApiRequest buildUpdateRequest() {
        return UpdateTaskApiRequest.builder()
                .name("Name after update")
                .description("Description after update")
                .confidential(true)
                .deadline(LocalDateTime.now())
                .executorId(UUID.randomUUID().toString())
                .priority(Priority.LOW.name())
                .sharedWith(Set.of(UUID.randomUUID().toString(), UUID.randomUUID().toString()))
                .status(TaskStatus.CANCELLED.name())
                .build();
    }

    private TaskEntity buildTaskEntity() {
        return TaskEntity.builder()
                .name("Name of task")
                .description("Description of task")
                .priority(Priority.HIGH)
                .reporterId(UUID.randomUUID().toString())
                .executorId(UUID.randomUUID().toString())
                .confidential(false)
                .sharedWith(Set.of(UUID.randomUUID().toString(), UUID.randomUUID().toString()))
                .deadline(LocalDateTime.now())
                .taskId(UUID.randomUUID().toString())
                .status(TaskStatus.TO_DO)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}