package com.madnan.pfbackenddemo.model;

public enum OperationStatus {

    APPROVED("Approved"),
    DENIED("Denied"),
    PENDING("Pending"),
    CANCEL("Cancel");

    private final String state;

    OperationStatus(String state) {
        this.state = state;
    }
}
