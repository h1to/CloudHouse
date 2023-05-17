package com.cloudhouse.booking.service.client;

import ch.qos.logback.classic.Logger;
import com.cloudhouse.booking.entity.Response;
import com.cloudhouse.booking.entity.booking.*;
import com.cloudhouse.booking.repository.*;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static com.cloudhouse.booking.config.StaticDataLoader.getUserId;

@Service
public class BookingService {

    private BookingRepo bookingRepo;
    private AddServiceRepo addServiceRepo;
    private AgeGroupRepo ageGroupRepo;
    private KidsRepo kidsRepo;

    private RoomService roomService;
    private RoomTypeService roomTypeService;


    @Value("${booking.history.period}")
    private long bookingHistoryPeriod;

    private static final Logger logger = (Logger) LoggerFactory.getLogger(BookingService.class);

    @Autowired
    public BookingService(KidsRepo kidsRepo,
                          BookingRepo bookingRepo,
                          AddServiceRepo addServiceRepo,
                          AgeGroupRepo ageGroupRepo,
                          RoomService roomService,
                          RoomTypeService roomTypeService) {
        this.bookingRepo = bookingRepo;
        this.addServiceRepo = addServiceRepo;
        this.ageGroupRepo = ageGroupRepo;
        this.roomService = roomService;
        this.roomTypeService = roomTypeService;
        this.kidsRepo = kidsRepo;
    }

    @CircuitBreaker(name = "bookingService")
    public List<Booking> getAllBookings() {
        return bookingRepo.findAllByStatusNotAndCheckInAfter(EBookingStatus.NEW,
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
        List<Room> rooms = roomService.getRoomsByType(roomTypeService.findAllByPersons(persons));
        if (rooms == null || rooms.isEmpty()) {
            return rooms;
        }

        List<Long> bookedRoomIds = bookingRepo.findBookingsOverlap(checkIn, checkOut);
        if (bookedRoomIds.isEmpty()) {
            return rooms;
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

            if (rooms == null || rooms.isEmpty()) {
                response.setStatusCode(300);
                response.setMsg("No available rooms left");
                return response;
            }
            booking.setRoom(rooms.get(0));
        }
        else {
            booking.setRoom(roomService.getRoomsExceptAndRoomType(bookedRoomIds, booking.getRoom().getRoomType()).get(0));
        }

        if (booking.getAdults() > booking.getRoom().getRoomType().getPersons()) {
            response.setMsg("Maximum persons for type of room " + booking.getRoom().getRoomType().getTypeName() + ": " + booking.getRoom().getRoomType().getPersons());
            response.setStatusCode(301);
            return response;
        }

        booking.setCreationDate(LocalDateTime.now());
        booking.setStatus(EBookingStatus.NEW);
        booking.setUserId(getUserId());
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
            oldBooking.getKids();
            for (int i =0; i < oldBooking.getKids().size(); i++) {
                if (oldBooking.getKids().get(i).getIdKids() == booking.getKids().get(i).getIdKids()) {
                    oldBooking.getKids().get(i).setNumberOfKids(booking.getKids().get(i).getNumberOfKids());
                }
            }


            response.setData(calculateTotalPrice(oldBooking));
            bookingRepo.flush();

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
        booking.setStatus(EBookingStatus.CANCELLED);
        bookingRepo.flush();
        response.setData(booking);
        response.setStatusCode(0);
        response.setMsg("Success");

        return response;
    }

    @CircuitBreaker(name = "bookingService")
    public Response<List<AgeGroup>> getAgeGroups() {
        Response<List<AgeGroup>> response = new Response<>();
        response.setData(ageGroupRepo.findAll());
        response.setStatusCode(0);
        response.setMsg("Success");
        return response;
    }


    @CircuitBreaker(name = "bookingService")
    public Response<List<AdditionalService>> getAddServices() {
        Response<List<AdditionalService>> response = new Response<>();
        response.setData(addServiceRepo.findAll());
        response.setStatusCode(0);
        response.setMsg("Success");
        return response;
    }

    @CircuitBreaker(name = "bookingService")
    public Response<List<RoomType>> getRoomTypes()  {
        Response<List<RoomType>> response = new Response<>();
        response.setData(roomTypeService.findAll());
        response.setStatusCode(0);
        response.setMsg("Success");
        return response;
    }


    @CircuitBreaker(name = "bookingService")
    public Response<List<Booking>> getCheckInToday() {
        Response<List<Booking>> response = new Response<>();
        response.setData(bookingRepo.findAllByCheckIn(LocalDateTime.now()));
        response.setStatusCode(0);
        response.setMsg("Success");
        return response;
    }

    @CircuitBreaker(name = "bookingService")
    public Response<List<Booking>> getCheckOutToday() {
        Response<List<Booking>> response = new Response<>();
        response.setData(bookingRepo.findAllByCheckOut(LocalDateTime.now()));
        response.setStatusCode(0);
        response.setMsg("Success");
        return response;
    }

    @CircuitBreaker(name = "bookingService")
    public List<Booking> getUsersBookings(String user) {
        return bookingRepo.findAllByUserId(user);
    }

    @CircuitBreaker(name = "bookingService")
    public void deleteBooking(Long bookingID) {
        bookingRepo.deleteById(bookingID);
    }

    @CircuitBreaker(name = "bookingService")
    public Response<Booking> checkIn(Long idBooking) {
        Response<Booking> response = new Response<>();

        if (bookingRepo.existsById(idBooking)) {
            Booking booking = bookingRepo.findByIdBooking(idBooking);

            if (!booking.getCheckIn().toLocalDate().isEqual(LocalDate.now())) {
                response.setData(booking);
                response.setStatusCode(304);
                response.setMsg("Can not check-in because check-in date is not today");

                return response;
            }

            booking.setStatus(EBookingStatus.ACTIVE);
            booking.setCheckIn(LocalDateTime.now());
            booking.getRoom().setOccupied(true);
            bookingRepo.flush();

            response.setData(booking);
            response.setStatusCode(0);
            response.setMsg("Success");
            return response;
        }

        response.setStatusCode(404);
        response.setMsg("Booking does not exist");
        return response;
    }


    @CircuitBreaker(name = "bookingService")
    public Response<Booking> checkOut(Long idBooking) {
        Response<Booking> response = new Response<>();

        if (bookingRepo.existsById(idBooking)) {
            Booking booking = bookingRepo.findByIdBooking(idBooking);

            if (!booking.getCheckOut().toLocalDate().isEqual(LocalDate.now())) {
                response.setData(booking);
                response.setStatusCode(304);
                response.setMsg("Can not check-out because check-out date is not today");

                return response;
            }

            booking.setStatus(EBookingStatus.USED);
            booking.getRoom().setOccupied(false);
            booking.setCheckOut(LocalDateTime.now());
            bookingRepo.flush();

            response.setData(booking);
            response.setStatusCode(0);
            response.setMsg("Success");
            return response;
        }

        response.setStatusCode(404);
        response.setMsg("Booking does not exist");
        return response;
    }

    public Booking calculateTotalPrice(Booking booking) {
        BigDecimal totalPrice = roomTypeService.calcRoomPrice(booking);

        booking.setAdditionalServices(loadAdditionalServices(booking));
        List<AdditionalService> services = booking.getAdditionalServices();
        if (services != null && !services.isEmpty()) {
            int extraAdults = booking.getAdults() - 2;

            for (AdditionalService service : services) {
                if (service.getIdService() == 1) {
                    totalPrice = totalPrice.add(service.getPrice().multiply(BigDecimal.valueOf(booking.getAdults()))
                            .multiply(
                                    BigDecimal.valueOf(
                                            ChronoUnit.DAYS.between(booking.getCheckIn(),booking.getCheckOut()))));

                    logger.info("Price for living + adults meals: " + totalPrice);
                    if (booking.getKids() != null && !booking.getKids().isEmpty()) {
                        int kidsNumber = 0;
                        for (Kids kids : booking.getKids()) {
                            kidsNumber += kids.getNumberOfKids();
                        }
                        totalPrice = totalPrice.add(service.getKidsPrice().multiply(BigDecimal.valueOf(kidsNumber))
                                .multiply(
                                        BigDecimal.valueOf(
                                                ChronoUnit.DAYS.between(booking.getCheckIn(),booking.getCheckOut()))));
                    }
                    logger.info("Price for living + meals for everyone: " + totalPrice);
                    continue;
                }
                else if (service.getDaily()) {
                    if (service.getIdService() == 2) {
                        totalPrice = totalPrice.add(service.getPrice().multiply(BigDecimal.valueOf(extraAdults))
                                .multiply(BigDecimal.valueOf(ChronoUnit.DAYS.between(booking.getCheckIn(),booking.getCheckOut()))));
                        logger.info("+ extra bed for adults: " + totalPrice);
                        if (booking.getKids() != null && !booking.getKids().isEmpty()) {
                            for (Kids kids : booking.getKids()) {
                                if (kids.getAgeGroup().getIdAgeGroup() == 2) {
                                    totalPrice = totalPrice.add(service.getKidsPrice()
                                            .multiply(BigDecimal.valueOf(kids.getNumberOfKids()))
                                            .multiply(
                                                    BigDecimal.valueOf(
                                                            ChronoUnit.DAYS.between(booking.getCheckIn(),booking.getCheckOut()))));
                                }
                            }
                        }

                        logger.info("+ extra bed for kids: " + totalPrice);
                        continue;
                    }
                    totalPrice = totalPrice.add(service.getPrice());
                    logger.info("+ daily service: " + totalPrice);
                    continue;
                }
                totalPrice = totalPrice.add(service.getPrice());
                logger.info("+ service: " + totalPrice);
            }
        }

        booking.setTotalPrice(totalPrice);
        return booking;
    }

    @CircuitBreaker(name = "bookingService")
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
