package com.cloudhouse.booking.entity.booking;

public enum EBookingStatus {

    NEW("NEW"),
    CONFIRMED("CONFIRMED"),
    ACTIVE("ACTIVE"),
    CANCELLED("CANCELLED"),
    USED("USED");

    private String title;

    EBookingStatus(String title) {
        this.title = title;
    }
}
