package com.cloudhouse.booking.entity.booking;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRoom;

    @Column(nullable = false)
    private String roomNum;

    @ManyToOne
    @JoinColumn(name = "id_building")
    private Building building;

    @ManyToOne
    @JoinColumn(name = "id_type")
    private RoomType roomType;

    @Column(nullable = false)
    private Integer persons;

    @Column(nullable = false)
    private Boolean clean;

    @Column(nullable = false)
    private Boolean occupied;

}
