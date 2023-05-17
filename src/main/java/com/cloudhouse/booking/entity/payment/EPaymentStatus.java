package com.cloudhouse.booking.entity.payment;

public enum EPaymentStatus {

    CANCELLED("CANCELLED"),
    PENDING("PENDING"),
    SUCCESS("SUCCESS");

    private String title;

    EPaymentStatus(String title) {
        this.title = title;
    }
}
