package com.cloudhouse.booking.entity.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idBooking;

    @Column
    private String users;

    @OneToOne
    @JoinColumn(name = "id_room")
    private Room room;

    @Column(nullable = false)
    private LocalDateTime checkIn;

    @Column(nullable = false)
    private LocalDateTime checkOut;

    @Column(nullable = false)
    private int adults;

    @OneToMany
    @JoinColumn(name = "idKids")
    private List<Kids> kids;

    @ManyToMany
    @JoinColumn(name = "id_service")
    private List<AdditionalService> additionalServices;

    @ManyToOne
    @JoinColumn(name = "idBookingStatus")
    private BookingStatus status;

    @Column(nullable = false)
    private BigDecimal totalPrice;

}
