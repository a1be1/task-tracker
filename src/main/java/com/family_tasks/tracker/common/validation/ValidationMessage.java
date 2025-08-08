package com.family_tasks.tracker.common.validation;

import static com.family_tasks.tracker.common.validation.ValidationConstants.USER_NAME_MAX_LENGTH;

public interface ValidationMessage {

    String USER_NAME_NOT_SPECIFIED = "A user name isn't specified.";
    String USER_NAME_TOO_LONG = "A user name length shouldn't be more than " + USER_NAME_MAX_LENGTH + ".";
    String IS_ADMIN_NOT_SPECIFIED = "A user admin flag isn't specified.";
}
