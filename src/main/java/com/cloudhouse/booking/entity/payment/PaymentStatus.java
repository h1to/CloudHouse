package com.cloudhouse.booking.entity.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class PaymentStatus {
    @Id
    private Long idPaymentStatus;

    private EStatus status;

    @Column(length = 200)
    private String description;

}
