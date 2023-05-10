package com.cloudhouse.booking.entity.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class RoomType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idType;

    @Column(nullable = false, length = 50)
    private String typeName;

    @OneToMany
    @JoinColumn(name = "idPricePeriod")
    private List<PricePeriod> price;

}
