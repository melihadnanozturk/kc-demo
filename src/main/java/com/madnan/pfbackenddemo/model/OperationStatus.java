package com.madnan.pfbackenddemo.model;

public enum OperationStatus {

    APPROVED("Approved"),
    DENIED("Denied"),
    //REVIEW("Review"),
    PENDING("Pending");

    private final String state;

    OperationStatus(String state) {
        this.state = state;
    }
}
