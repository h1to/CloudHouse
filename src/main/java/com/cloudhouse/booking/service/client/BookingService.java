package com.cloudhouse.booking.service.client;

import ch.qos.logback.classic.Logger;
import com.cloudhouse.booking.entity.Response;
import com.cloudhouse.booking.entity.booking.*;
import com.cloudhouse.booking.repository.AddServiceRepo;
import com.cloudhouse.booking.repository.BookingRepo;
import com.cloudhouse.booking.repository.BookingStatusRepo;
import com.cloudhouse.booking.repository.RoomRepo;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.cloudhouse.booking.config.StaticDataLoader.bookingStatuses;
import static com.cloudhouse.booking.config.StaticDataLoader.getUserId;

@Service
public class BookingService {

    private BookingRepo bookingRepo;
    private AddServiceRepo addServiceRepo;
    private BookingStatusRepo bookingStatusRepo;
    private RoomService roomService;
    private RoomTypeService roomTypeService;


    @Value("${booking.history.period}")
    private long bookingHistoryPeriod;

    private static final Logger logger = (Logger) LoggerFactory.getLogger(BookingService.class);

    @Autowired
    public BookingService(BookingRepo bookingRepo, AddServiceRepo addServiceRepo, BookingStatusRepo bookingStatusRepo, RoomService roomService, RoomTypeService roomTypeService) {
        this.bookingRepo = bookingRepo;
        this.addServiceRepo = addServiceRepo;
        this.bookingStatusRepo = bookingStatusRepo;
        this.roomService = roomService;
        this.roomTypeService = roomTypeService;
    }

    @CircuitBreaker(name = "bookingService")
    public List<Booking> getAllBookings() {
        return bookingRepo.findAllByStatusNotAndCheckInAfter(bookingStatuses.stream()
                .filter(s -> s.getStatus().compareTo(EBookingStatus.NEW) == 0).findFirst().get(),
                LocalDateTime.now().minusMonths(bookingHistoryPeriod));
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
        List<Long> bookedRoomIds = bookingRepo.findBookingsOverlap(checkIn, checkOut, roomType);
        if (bookedRoomIds.isEmpty()) {
            return roomService.getRoomsByType(roomTypeService.findRoomTypeById(roomType));
        }

        return roomService.getRoomsExceptAndRoomType(bookedRoomIds, roomTypeService.findRoomTypeById(roomType));
    }

    @CircuitBreaker(name = "bookingService")
    public List<Room> getAvailableRooms(LocalDateTime checkIn, LocalDateTime checkOut, Integer persons) {
        List<Long> bookedRoomIds = bookingRepo.findBookingsOverlap(checkIn, checkOut);
        if (bookedRoomIds.isEmpty()) {
            return roomService.getRoomsByType(roomTypeService.findAllByPersons(persons));
        }

        return roomService.getRoomsExcept(bookedRoomIds);
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

        if (booking.getAdults() > booking.getRoom().getRoomType().getPersons()) {
            response.setMsg("Maximum persons for type of room " + booking.getRoom().getRoomType().getTypeName() + ": " + booking.getRoom().getRoomType().getPersons());
            response.setStatusCode(301);
            return response;
        }

        booking.setCreationDate(LocalDateTime.now());
        booking.setStatus(bookingStatuses.stream().filter(s -> s.getStatus().compareTo(EBookingStatus.NEW) == 0).findFirst().get());
        booking.setUsers(getUserId());
        booking.setPayed(false);

        bookingRepo.save(calculateTotalPrice(booking));
        response.setStatusCode(0);
        response.setMsg("Success");
        return response;
    }

    @CircuitBreaker(name = "bookingService")
    public Response<Booking> updateBooking(Booking booking) {
        Response<Booking> response = new Response<>();
        if (bookingRepo.existsById(booking.getIdBooking())) {
            Booking oldBooking = bookingRepo.findByIdBooking(booking.getIdBooking());
            oldBooking.setAdditionalServices(loadAdditionalServices(booking));
            oldBooking.setThreeTimesMeal(booking.getThreeTimesMeal());

            response.setData(bookingRepo.save(calculateTotalPrice(oldBooking)));
            response.setStatusCode(0);
            response.setMsg("Success");
            return response;
        }
        response.setData(booking);
        response.setStatusCode(404);
        response.setMsg("Booking does not exist");
        return response;
    }

    @CircuitBreaker(name = "bookingService")
    public Response<Booking> cancelBooking(Long bookingID) {
        Response<Booking> response = new Response<>();

        Booking booking = bookingRepo.findByIdBooking(bookingID);
        booking.setStatus(bookingStatuses.stream().filter( s -> s.getStatus().compareTo(EBookingStatus.CANCELLED) == 0).findFirst().get());
        response.setData(bookingRepo.save(booking));
        response.setStatusCode(0);
        response.setMsg("Success");

        return response;
    }

    public Booking calculateTotalPrice(Booking booking) {
        BigDecimal totalPrice = roomTypeService.calcRoomPrice(booking);

        List<Long> serviceIds = new ArrayList<>();
        if (booking.getAdditionalServices() != null && !booking.getAdditionalServices().isEmpty()) {
            for (AdditionalService service : booking.getAdditionalServices()) {
                serviceIds.add(service.getIdService());
            }
        }


        booking.setAdditionalServices(addServiceRepo.findAllByIdServiceIn(serviceIds));
        List<AdditionalService> services = booking.getAdditionalServices();
        if (services != null && !services.isEmpty()) {
            for (AdditionalService service : services) {
                totalPrice = totalPrice.add(service.getPrice());
            }
        }

        booking.setTotalPrice(totalPrice);
        return booking;
    }

    public List<Booking> getUsersBookings(String user) {
        return bookingRepo.findAllByUsers(user);
    }

    public void deleteBooking(Long bookingID) {
        bookingRepo.deleteById(bookingID);
    }

    private List<AdditionalService> loadAdditionalServices(Booking booking) {
        List<Long> serviceIds = new ArrayList<>();
        if (booking.getAdditionalServices() != null && !booking.getAdditionalServices().isEmpty()) {
            for (AdditionalService service : booking.getAdditionalServices()) {
                serviceIds.add(service.getIdService());
            }
        }

        return addServiceRepo.findAllByIdServiceIn(serviceIds);
    }

}
