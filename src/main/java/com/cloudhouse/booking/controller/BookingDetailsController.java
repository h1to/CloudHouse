package com.cloudhouse.booking.controller;

import ch.qos.logback.classic.Logger;
import com.cloudhouse.booking.entity.Response;
import com.cloudhouse.booking.entity.booking.AdditionalService;
import com.cloudhouse.booking.entity.booking.AgeGroup;
import com.cloudhouse.booking.entity.booking.RoomType;
import com.cloudhouse.booking.service.client.BookingService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("details")
public class BookingDetailsController {

    private BookingService bookingService;

    private static final Logger logger = (Logger) LoggerFactory.getLogger(BookingDetailsController.class);

    @Autowired
    public BookingDetailsController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("room/types")
    public Response<List<RoomType>> getAllRoomTypes() {
        return bookingService.getRoomTypes();
    }

    @GetMapping("services")
    public Response<List<AdditionalService>> getAllAddServices() {
        return bookingService.getAddServices();
    }

    @GetMapping("age/groups")
    public Response<List<AgeGroup>> getAllAgeGroups() {
        return bookingService.getAgeGroups();
    }

}
