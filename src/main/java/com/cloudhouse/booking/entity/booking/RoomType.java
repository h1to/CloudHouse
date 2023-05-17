package com.cloudhouse.booking.entity.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
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

    @Column(nullable = false)
    private Integer persons;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "idPricePeriod")
    private List<PricePeriod> price;

}
