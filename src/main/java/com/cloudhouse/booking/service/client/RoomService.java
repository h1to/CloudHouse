package com.cloudhouse.booking.service.client;

import ch.qos.logback.classic.Logger;
import com.cloudhouse.booking.entity.booking.Booking;
import com.cloudhouse.booking.entity.booking.Room;
import com.cloudhouse.booking.entity.booking.RoomType;
import com.cloudhouse.booking.repository.RoomRepo;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomService {

    private RoomRepo roomRepo;

    private static final Logger logger = (Logger) LoggerFactory.getLogger(BookingService.class);

    @Autowired
    public RoomService(RoomRepo roomRepo) {
        this.roomRepo = roomRepo;
    }

    public List<Room> getAllRooms() {
        return roomRepo.findAll();
    }

    public List<Room> getRoomsByType(RoomType roomType) {
        return roomRepo.findAllByRoomType(roomType);
    }

    public List<Room> getRoomsByType(List<RoomType> types) {
        return roomRepo.findAllByRoomTypeIn(types);
    }

    public List<Room> getRoomsExcept(List<Long> bookedRoomIds) {
        return roomRepo.findAllByIdRoomIsNotIn(bookedRoomIds);
    }

    public List<Room> getRoomsExceptAndRoomType(List<Long> bookedRoomIds, RoomType roomType) {
        return roomRepo.findAllByIdRoomIsNotInAndRoomType(bookedRoomIds, roomType);
    }

}
