package com.cloudhouse.booking.entity.booking;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Kids {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idKids;

    @ManyToOne
    @JoinColumn(name = "id_age_group")
    private AgeGroup ageGroup;

    @Column(nullable = false)
    private int numberOfKids;

}
