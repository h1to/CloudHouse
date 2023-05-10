package com.cloudhouse.booking.service.client;

import ch.qos.logback.classic.Logger;
import com.cloudhouse.booking.entity.Response;
import com.cloudhouse.booking.entity.booking.*;
import com.cloudhouse.booking.repository.BookingRepo;
import com.cloudhouse.booking.repository.BookingStatusRepo;
import com.cloudhouse.booking.repository.RoomRepo;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {

    private BookingRepo bookingRepo;

    private BookingStatusRepo bookingStatusRepo;

    private RoomService roomService;

    private RoomTypeService roomTypeService;

    private static final Logger logger = (Logger) LoggerFactory.getLogger(BookingService.class);

    @Autowired
    public BookingService(BookingRepo bookingRepo, BookingStatusRepo bookingStatusRepo, RoomService roomService, RoomTypeService roomTypeService) {
        this.bookingRepo = bookingRepo;
        this.bookingStatusRepo = bookingStatusRepo;
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

    @CircuitBreaker(name = "bookingService")
    public Response<Booking> createBooking(Booking booking) {
        Response<Booking> response = new Response<>();
        response.setData(booking);

        List<Long> bookedRoomIds = bookingRepo.findBookingsOverlap(
                booking.getCheckIn(),
                booking.getCheckOut(),
                booking.getRoom().getRoomType().getIdType());

        if (bookedRoomIds.isEmpty()) {
            List<Room> rooms = roomService.getRoomsByType(booking.getRoom().getRoomType());

            if (rooms.isEmpty()) {
                response.setStatusCode(300);
                response.setMsg("No available rooms left");
                return response;
            }
            booking.setRoom(rooms.get(0));
        }
        booking.setRoom(roomService.getRoomsExceptAndRoomType(bookedRoomIds, booking.getRoom().getRoomType()).get(0));
        booking.setStatus(bookingStatusRepo.findAllByStatus(EBookingStatus.NEW));

        bookingRepo.save(booking);
        response.setStatusCode(0);
        response.setMsg("Success");
        return response;
    }

    public Booking calculateTotalPrice(Booking booking) {
        BigDecimal totalPrice = new BigDecimal(0);

        totalPrice = roomTypeService.calcRoomPrice(booking.getCheckIn(), booking.getCheckOut(), booking.getRoom().getRoomType());

        List<AdditionalService> services = booking.getAdditionalServices();
        for (int i = 0; i < services.size(); i++) {
            totalPrice = totalPrice.add(services.get(i).getPrice());
        }
        booking.setTotalPrice(totalPrice);
        return booking;
    }

    public List<Booking> getUsersBookings(String user) {
        return bookingRepo.findAllByUsers(user);
    }

}
