package com.family_tasks.tracker.task.model.entity;

import com.family_tasks.tracker.common.TableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static com.family_tasks.tracker.common.TableNames.EXECUTORS_TASKS_TABLE;

@Entity(name = TableNames.TASKS_TABLE)
@Setter
@Getter
@ToString
@NoArgsConstructor
public class TaskEntity {
    @Id
    private String id;
    private String status;
    private String name;
    private String description;
    private String priority;
    private Integer reporterId;
    @ElementCollection
    @CollectionTable(
            name = EXECUTORS_TASKS_TABLE,
            joinColumns = @JoinColumn(name = "task_id")
    )
    @Column(name = "user_id")
    private Set<Integer> executorIds = new HashSet<>();
    private boolean confidential;
    private Integer rewardsPoints;
    private LocalDate deadline;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}