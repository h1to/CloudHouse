package com.cloudhouse.booking.controller;

import ch.qos.logback.classic.Logger;
import com.cloudhouse.booking.entity.booking.Room;
import com.cloudhouse.booking.service.client.BookingService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("available")
public class AvailabilityController {

    private BookingService bookingService;

    private static final Logger logger = (Logger) LoggerFactory.getLogger(BookingController.class);

    @Autowired
    public AvailabilityController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("rooms")
    public List<Room> findAvailableRooms(@RequestParam(value = "checkIn")
                                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                         LocalDateTime checkIn,
                                         @RequestParam(value = "checkOut")
                                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                         LocalDateTime checkOut,
                                         @RequestParam(value = "roomType", required = false) Long roomType,
                                         @RequestParam(value = "persons", required = false) Integer persons) {

        if (roomType == null && persons == null) {
            return bookingService.getAvailableRooms(checkIn,checkOut);
        }
        else if (roomType != null && persons == null) {
            return bookingService.getAvailableRooms(checkIn, checkOut, roomType);
        }
        else if (roomType == null && persons != null) {
            return bookingService.getAvailableRooms(checkIn, checkOut, persons);
        }
        return bookingService.getAvailableRooms(checkIn, checkOut, roomType, persons);

    }

}
