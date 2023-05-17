package com.cloudhouse.booking.entity.payment;

import com.cloudhouse.booking.entity.booking.Booking;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDateTime;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Payment {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPayment;

    @ManyToOne
    @JoinColumn(name = "id_booking", updatable = false)
    private Booking booking;

    @Column(nullable = false, updatable = false)
    private BigDecimal amount;

    @Column(name = "date", nullable = false, updatable = false)
    private Date dateTime;

    @Column(nullable = false)
    private EPaymentType paymentType;

    @Column(nullable = false)
    private EPaymentStatus status;

}

