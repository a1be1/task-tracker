package com.family_tasks.tracker.common.error.exception;

import static com.family_tasks.tracker.common.validation.ValidationMessage.TASK_NOT_EXIST;
import static com.family_tasks.tracker.common.validation.ValidationMessage.USER_NOT_EXIST;

public class NotFoundException extends RuntimeException {

    private NotFoundException(String message) {
        super(message);
    }

    public static NotFoundException taskNotFound(String taskId) {
        return new NotFoundException(String.format(TASK_NOT_EXIST, taskId));
    }

    public static NotFoundException userNotFound(Integer userId) {
        return new NotFoundException(String.format(USER_NOT_EXIST, userId));
    }
}