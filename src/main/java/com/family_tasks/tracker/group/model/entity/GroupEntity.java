package com.family_tasks.tracker.group.model.entity;

import com.family_tasks.tracker.common.TableNames;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity(name = TableNames.GROUPS)
@Setter
@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class GroupEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer ownerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}