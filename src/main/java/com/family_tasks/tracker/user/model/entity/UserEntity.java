package com.family_tasks.tracker.user.model.entity;

import com.family_tasks.tracker.common.TableNames;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;


@Entity(name = TableNames.USERS_TABLE)
@Getter
@Setter
@ToString
@NoArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private boolean admin;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}