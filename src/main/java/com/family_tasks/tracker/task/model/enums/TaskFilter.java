package com.family_tasks.tracker.task.model.enums;


import lombok.Getter;

@Getter
public enum TaskFilter {
    IS_REPORTER_ACTIVE_TASK("isReporterActiveTask"),
    IS_EXECUTOR_ACTIVE_TASK("isExecutorActiveTask"),
    IS_REPORTER_COMPLETED_TASK("isReporterCompletedTask"),
    IS_EXECUTOR_COMPLETED_TASK("isExecutorCompletedTask"),
    ALL_AVAILABLE("allAvailable"),
    ALL_CANCELLED("allCancelled");

    private final String value;

    TaskFilter(String value) {
        this.value = value;
    }

    public static TaskFilter fromValue(String value) {
        for (TaskFilter filter : values()) {
            if (filter.value.equalsIgnoreCase(value)) {
                return filter;
            }
        }
        throw new IllegalArgumentException("Unknown filter: " + value);
    }
}