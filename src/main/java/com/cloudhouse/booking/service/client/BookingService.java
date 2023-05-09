package com.cloudhouse.booking.service.client;

import ch.qos.logback.classic.Logger;
import com.cloudhouse.booking.entity.Response;
import com.cloudhouse.booking.entity.booking.Booking;
import com.cloudhouse.booking.entity.booking.Room;
import com.cloudhouse.booking.entity.booking.RoomType;
import com.cloudhouse.booking.repository.BookingRepo;
import com.cloudhouse.booking.repository.RoomRepo;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {

    private BookingRepo bookingRepo;

    private RoomService roomService;

    private RoomTypeService roomTypeService;

    private static final Logger logger = (Logger) LoggerFactory.getLogger(BookingService.class);

    @Autowired
    public BookingService(BookingRepo bookingRepo, RoomService roomService, RoomTypeService roomTypeService) {
        this.bookingRepo = bookingRepo;
        this.roomService = roomService;
        this.roomTypeService = roomTypeService;
    }

    @CircuitBreaker(name = "bookingService")
    public List<Room> getAvailableRooms(LocalDateTime checkIn, LocalDateTime checkOut) {
        List<Long> bookedRoomIds = bookingRepo.findBookingsOverlap(checkIn, checkOut);
        if (bookedRoomIds.isEmpty()) {
            return roomService.getAllRooms();
        }

        return roomService.getRoomsExcept(bookedRoomIds);
    }

    @CircuitBreaker(name = "bookingService")
    public List<Room> getAvailableRooms(LocalDateTime checkIn, LocalDateTime checkOut, Long roomType) {
        List<Long> bookedRoomIds = bookingRepo.findBookingsOverlap(checkIn, checkOut);
        if (bookedRoomIds.isEmpty()) {
            return roomService.getRoomsByType(roomTypeService.findRoomTypeById(roomType));
        }

        return roomService.getRoomsExceptAndRoomType(bookedRoomIds, roomTypeService.findRoomTypeById(roomType));
    }

    @CircuitBreaker(name = "bookingService")
    public List<Room> getAvailableRooms(LocalDateTime checkIn, LocalDateTime checkOut, Long roomType, Integer persons) {
        List<Long> bookedRoomIds = bookingRepo.findBookingsOverlap(checkIn, checkOut, roomType, persons);
        if (bookedRoomIds.isEmpty()) {
            return roomService.getRoomsByTypeAndPersons(roomTypeService.findRoomTypeById(roomType), persons);
        }

        return roomService.getRoomsExceptByPersonsAndRoomType(bookedRoomIds, roomTypeService.findRoomTypeById(roomType), persons);
    }

    @CircuitBreaker(name = "bookingService")
    public List<Room> getAvailableRooms(LocalDateTime checkIn, LocalDateTime checkOut, Integer persons) {
        List<Long> bookedRoomIds = bookingRepo.findBookingsOverlap(checkIn, checkOut, persons);
        if (bookedRoomIds.isEmpty()) {
            return roomService.getRoomsByPersons(persons);
        }

        return roomService.getRoomsExceptAndPersons(bookedRoomIds, persons);
    }

//    @CircuitBreaker(name = "bookingService")
//    public Response<Booking> createBooking(Booking booking) {
//        Response<Booking> response = new Response<>();
//        response.setData(booking);
//
//        List<Long> bookedRoomIds = bookingRepo.findBookingsOverlapByRoomAndDate(
//                booking.getCheckIn(),
//                booking.getCheckOut(),
//                booking.getRoom().getIdRoom());
//
//        if (bookedRoomIds.isEmpty()) {
//            return roomService.getAllRooms();
//        }
//
//        bookingRepo.save(booking);
//        response.setStatusCode(0);
//        response.setMsg("Success");
//        return response;
//    }

}
