package com.family_tasks.tracker.common.utils;

import static com.family_tasks.tracker.common.validation.ValidationMessage.ID_HAS_INVALID_FORMAT;

public class ValidateUtils {
    public static Integer parseId(String stringId) {
        try {
            return Integer.parseInt(stringId);
        } catch (NumberFormatException exception) {
            throw new NumberFormatException(ID_HAS_INVALID_FORMAT);
        }
    }
}