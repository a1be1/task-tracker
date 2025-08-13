package com.family_tasks.tracker.common.validation;

import static com.family_tasks.tracker.common.validation.ValidationConstants.*;

public interface ValidationMessage {

    String USER_NAME_NOT_SPECIFIED = "A user name isn't specified.";
    String USER_NAME_TOO_LONG = "A user name length shouldn't be more than " + USER_NAME_MAX_LENGTH + ".";
    String IS_ADMIN_NOT_SPECIFIED = "A user admin flag isn't specified.";
    String TASK_NAME_NOT_SPECIFIED = "A task name isn't specified.";
    String TASK_NAME_TO_LONG = "A task name length shouldn't be more than " + TASK_NAME_MAX_LENGTH + ".";
    String TASK_DESCRIPTION_TO_LONG = "A task description length shouldn't be more than " + TASK_DESCRIPTION_MAX_LENGTH + ".";
    String TASK_DESCRIPTION_TO_SHORT = "A task description length shouldn't be less than " + TASK_DESCRIPTION_MIN_LENGTH + ".";
    String TASK_CONFIDENTIAL_STATUS_NOT_SPECIFIED = "A task confidential status isn't specified.";
    String TASK_DEADLINE_DATE_NOT_FUTURE = "Date must be in future";
    String TASK_PRIORITY_INVALID = "Invalid priority value. Please enter a valid priority.";
    String TASK_STATUS_INVALID = "Invalid status value. Please enter a valid status.";
    String TASK_STATUS_NULL = "Status must not be null";
    String TASK_PRIORITY_NULL = "Priority cannot be null";
}
