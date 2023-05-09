package com.cloudhouse.booking.entity.payment;

public enum EStatus {

    CANCELLED("CANCELLED"),
    PENDING("PENDING"),
    SUCCESS("SUCCESS");

    private String title;

    EStatus(String title) {
        this.title = title;
    }
}
