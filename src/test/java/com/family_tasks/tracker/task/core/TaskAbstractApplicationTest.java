package com.family_tasks.tracker.task.core;

import com.family_tasks.tracker.AbstractApplicationTest;
import com.family_tasks.tracker.task.application.TaskController;
import com.family_tasks.tracker.task.model.dto.CreateTaskApiRequest;
import com.family_tasks.tracker.task.model.dto.CreateTaskApiResponse;
import com.family_tasks.tracker.task.model.enums.Priority;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class TaskAbstractApplicationTest extends AbstractApplicationTest {

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
        assertThat(response.getPriority()).isEqualTo(request.getPriority());
        assertThat(response.getReporter()).isEqualTo(request.getReporter());
        assertThat(response.getExecutor()).isEqualTo(request.getExecutor());
        assertThat(response.getIsPrivate()).isEqualTo(request.getIsPrivate());
        assertThat(response.getSharedWith()).isEqualTo(request.getSharedWith());
        assertThat(response.getDeadline()).isEqualTo(request.getDeadline());
    }

    private CreateTaskApiRequest buildRequest() {
        return CreateTaskApiRequest.builder()
                .name("Name of task")
                .description("Description of task")
                .priority(Priority.HIGH)
                .reporter(null)
                .executor(null)
                .isPrivate(true)
                .sharedWith(null)
                .deadline(LocalDateTime.now())
                .build();
    }
}