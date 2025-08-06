package com.family_tasks.tracker.task.application;

import com.family_tasks.tracker.AbstractApplicationTest;
import com.family_tasks.tracker.task.model.dto.CreateTaskApiRequest;
import com.family_tasks.tracker.task.model.dto.CreateTaskApiResponse;
import com.family_tasks.tracker.task.model.enums.Priority;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link TaskController}
 */
public class TaskControllerTest extends AbstractApplicationTest {

    @Autowired
    protected TestRestTemplate client;

    @Test
    void createTask() {
        //prepare
        CreateTaskApiRequest request = buildRequest();
        //execute
        ResponseEntity<CreateTaskApiResponse> responseEntity = client.postForEntity(TaskController.TASK_URL, request, CreateTaskApiResponse.class);
        //validate
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        CreateTaskApiResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.getTaskId()).isNotNull();
        assertThat(response.getName()).isEqualTo(request.getName());
        assertThat(response.getDescription()).isEqualTo(request.getDescription());
        assertThat(response.getPriority().name()).isEqualTo(request.getPriority());
        assertThat(response.getReporterId()).isEqualTo(request.getReporterId());
        assertThat(response.getExecutorId()).isEqualTo(request.getExecutorId());
        assertThat(response.isConfidential()).isEqualTo(request.isConfidential());
        assertThat(response.getSharedWith()).isEqualTo(request.getSharedWith());
        assertThat(response.getDeadline()).isEqualTo(request.getDeadline());
    }

    private CreateTaskApiRequest buildRequest() {
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
}