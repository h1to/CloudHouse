package com.cloudhouse.booking.entity.payment;


public enum EPaymentType {

    CASH("CASH"),
    NONCASH("NONCASH");

    private String title;

    EPaymentType(String type) {
        this.title = type;
    }
}
