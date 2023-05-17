package com.cloudhouse.booking.entity.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;

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

    @Column(nullable = false)
    private LocalDateTime creationDate;

    @Column(nullable = false)
    private String userId;

    @OneToOne
    @JoinColumn(name = "id_room")
    private Room room;

    @Column(nullable = false)
    private LocalDateTime checkIn;

    @Column(nullable = false)
    private LocalDateTime checkOut;

    @Column(nullable = false)
    private int adults;

    @ManyToMany
    @Cascade({org.hibernate.annotations.CascadeType.ALL})
    @JoinColumn(name = "idKids")
    private List<Kids> kids;

    @Column(nullable = false)
    private Boolean threeTimesMeal;

    @ManyToMany
    @JoinColumn(name = "id_service")
    private List<AdditionalService> additionalServices;

    @Column(nullable = false)
    private EBookingStatus status;

    @Column(nullable = false)
    private Boolean payed;

    @Column(nullable = false)
    private BigDecimal totalPrice;

}
