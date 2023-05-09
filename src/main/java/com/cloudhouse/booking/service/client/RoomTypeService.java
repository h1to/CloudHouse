package com.cloudhouse.booking.service.client;

import com.cloudhouse.booking.entity.booking.RoomType;
import com.cloudhouse.booking.repository.RoomTypeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoomTypeService {

    private RoomTypeRepo roomTypeRepo;

    @Autowired
    public RoomTypeService(RoomTypeRepo roomTypeRepo) {
        this.roomTypeRepo = roomTypeRepo;
    }


    public RoomType findRoomTypeById(Long id) {
        return roomTypeRepo.findByIdType(id);
    }

}
