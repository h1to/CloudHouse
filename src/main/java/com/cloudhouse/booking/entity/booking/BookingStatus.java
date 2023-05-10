package com.cloudhouse.booking.entity.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class BookingStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idBookingStatus;

    @Column(nullable = false)
    private EBookingStatus status;

    @Column(length = 200)
    private String description;

    public BookingStatus(EBookingStatus status) {
        this.status = status;
    }

    public BookingStatus(EBookingStatus status, String description) {
        this.status = status;
        this.description = description;
    }
}
