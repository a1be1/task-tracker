package com.family_tasks.tracker.common.validation;

import static com.family_tasks.tracker.common.validation.ValidationConstants.*;

public interface ValidationMessage {

    String USER_NAME_NOT_SPECIFIED = "A user name isn't specified.";
    String USER_NAME_TOO_LONG = "A user name length shouldn't be more than " + USER_NAME_MAX_LENGTH + ".";
    String IS_ADMIN_NOT_SPECIFIED = "A user admin flag isn't specified.";
    String USER_NOT_EXIST = "User with id %s doesn't exist";
    String USER_NOT_SPECIFIED = "A user isn't specified.";
    String TASK_NAME_NOT_SPECIFIED = "A task name isn't specified.";
    String TASK_NAME_TOO_LONG = "A task name length shouldn't be more than " + TASK_NAME_MAX_LENGTH + ".";
    String TASK_DESCRIPTION_TOO_LONG = "A task description length shouldn't be more than " + TASK_DESCRIPTION_MAX_LENGTH + ".";
    String TASK_DESCRIPTION_TOO_SHORT = "A task description length shouldn't be less than " + TASK_DESCRIPTION_MIN_LENGTH + ".";
    String TASK_CONFIDENTIAL_STATUS_NOT_SPECIFIED = "A task confidential status isn't specified.";
    String TASK_DEADLINE_DATE_NOT_PRESENT_OR_FUTURE = "The deadline date must not be in the past";
    String TASK_PRIORITY_INVALID = "Invalid priority value. Please enter a valid priority.";
    String TASK_STATUS_INVALID = "Invalid status value. Please enter a valid status.";
    String TASK_STATUS_NULL = "Status must be specified.";
    String TASK_PRIORITY_NULL = "Priority must be specified.";
    String TASK_REPORTER_NULL = "Reporter must be required.";
    String TASK_NOT_EXIST = "Task with id %s doesn't exist.";
    String TASK_FILTER_INVALID = "Invalid filter value. Please enter a valid filter.";
    String TASK_FILTER_NOT_SPECIFIED = "A task's filter isn't specified.";
    String GROUP_OWNER_NOT_SPECIFIED = "An owner isn't specified.";
    String USER_ALREADY_IS_OWNER = "User with id %d is already the owner of another group.";
    String USER_ALREADY_HAS_GROUP = "This user is already a member of the group";
    String GROUP_NOT_EXIST = "Group with id %d doesn't exist.";
    String ID_HAS_INVALID_FORMAT = "The provided ID has an invalid format.";
    String GROUP_NOT_SPECIFIED = "A group isn't specified.";
    String CREATE_TASK_WITHOUT_GROUP = "To create a task you need to join a group or create a new one.";
    String CREATE_OR_UPDATE_TASK_FOR_OWN_GROUP = "A task can be created or updated only for users from own group.";
    String REWARDS_POINTS_POSITIVE = "Reward points cannot be negative.";
    String VALIDATION_FAILED = "Validation failed";
    String REWARD_NOT_EXIST = "Reward with id %s doesn't exist.";
}