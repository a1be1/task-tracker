package com.family_tasks.tracker.user.model.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class CreateUserApiRequest {

    private final String name;
    private final Boolean admin;
}
