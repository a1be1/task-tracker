package com.family_tasks.tracker.user.model.entity;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class UserEntity {

    private final String id;
    private final String name;
    private final boolean admin;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}