package com.madnan.pfbackenddemo.model;

public enum OperationVariables {
    STATUS("status"),
    OPERATION("operation"),
    TASK_NAME("taskName"),
    DELEGATE_EXPRESSION("delegateExpression"),
    USER_NAME("userName");

    private final String name;

    OperationVariables(String name) {
        this.name = name;
    }

}
