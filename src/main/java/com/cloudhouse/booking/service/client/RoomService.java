package com.cloudhouse.booking.service.client;

import ch.qos.logback.classic.Logger;
import com.cloudhouse.booking.entity.booking.Booking;
import com.cloudhouse.booking.entity.booking.Room;
import com.cloudhouse.booking.entity.booking.RoomType;
import com.cloudhouse.booking.repository.RoomRepo;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
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

    @CircuitBreaker(name = "bookingService")
    public List<Room> getAllRooms() {
        return roomRepo.findAll();
    }

    @CircuitBreaker(name = "bookingService")
    public List<Room> getRoomsByType(RoomType roomType) {
        return roomRepo.findAllByRoomType(roomType);
    }

    @CircuitBreaker(name = "bookingService")
    public List<Room> getRoomsByType(List<RoomType> types) {
        return roomRepo.findAllByRoomTypeIn(types);
    }

    @CircuitBreaker(name = "bookingService")
    public List<Room> getRoomsExcept(List<Long> bookedRoomIds) {
        return roomRepo.findAllByIdRoomIsNotIn(bookedRoomIds);
    }

    @CircuitBreaker(name = "bookingService")
    public List<Room> getRoomsExceptAndRoomType(List<Long> bookedRoomIds, RoomType roomType) {
        return roomRepo.findAllByIdRoomIsNotInAndRoomType(bookedRoomIds, roomType);
    }

    @CircuitBreaker(name = "bookingService")
    public void occupyRoom(Long idRoom) {
        Room room = roomRepo.findByIdRoom(idRoom);
        room.setOccupied(true);
        roomRepo.flush();
    }

    @CircuitBreaker(name = "bookingService")
    public void freeRoom(Long idRoom) {
        Room room = roomRepo.findByIdRoom(idRoom);
        room.setOccupied(false);
        roomRepo.flush();
    }

}
