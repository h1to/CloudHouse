package com.cloudhouse.booking.controller;

import ch.qos.logback.classic.Logger;
import com.cloudhouse.booking.entity.Response;
import com.cloudhouse.booking.entity.booking.Booking;
import com.cloudhouse.booking.service.client.BookingService;
import com.cloudhouse.booking.service.client.RoomService;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import static com.cloudhouse.booking.config.StaticDataLoader.getUserId;

@RestController
@RequestMapping("")
public class BookingController {

    private RoomService roomService;

    private BookingService bookingService;

    private static final Logger logger = (Logger) LoggerFactory.getLogger(BookingController.class);

    @Autowired
    public BookingController(RoomService roomService, BookingService bookingService) {
        this.roomService = roomService;
        this.bookingService = bookingService;
    }


    @RolesAllowed({"admin","manager"})
    @GetMapping("all")
    public Response<List<Booking>> getAllBookings() {
        Response<List<Booking>> response = new Response<>();

        response.setData(bookingService.getAllBookings());
        response.setStatusCode(0);
        response.setMsg("Success");
        return response;
    }

    @RolesAllowed({"admin","manager","guest"})
    @GetMapping("all/user")
    public Response<List<Booking>> getBookingsByUser() {
        Response<List<Booking>> response = new Response<>();

        KeycloakAuthenticationToken authentication =
                (KeycloakAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        Principal principal = authentication.getAccount().getPrincipal();

        if (principal instanceof KeycloakPrincipal) {
            KeycloakPrincipal<KeycloakSecurityContext> kPrincipal = (KeycloakPrincipal<KeycloakSecurityContext>) principal;

            AccessToken token = kPrincipal.getKeycloakSecurityContext().getToken();
            response.setData(bookingService.getUsersBookings(token.getEmail()));
        }

        response.setStatusCode(0);
        response.setMsg("Success");
        return response;
    }

    @RolesAllowed({"admin","manager","guest"})
    @PostMapping("create")
    public Response<Booking> createBooking(@RequestBody Booking booking) {
        return bookingService.createBooking(booking);
    }

    @RolesAllowed({"admin","manager","guest"})
    @PutMapping("update")
    public Response<Booking> updateBooking(@RequestBody Booking booking) {
        return bookingService.updateBooking(booking);
    }

    @RolesAllowed({"admin","manager","guest"})
    @PutMapping("cancel/{id}")
    public Response<Booking> cancelBooking(@PathVariable("id") Long bookingID) {
        return bookingService.cancelBooking(bookingID);
    }

    @RolesAllowed({"admin","manager"})
    @DeleteMapping("delete/{bookingID}")
    public Response deleteBooking(@PathVariable("bookingID") Long bookingID) {
        bookingService.deleteBooking(bookingID);
        Response response = new Response();
        response.setStatusCode(0);
        response.setMsg("Success");
        return response;
    }







    @RolesAllowed({"admin","manager","guest"})
    @GetMapping(path = "/users")
    public String getUserInfo() {

        KeycloakAuthenticationToken authentication =
                (KeycloakAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        Principal principal = authentication.getAccount().getPrincipal();

        String userIdByToken = "";

        if (principal instanceof KeycloakPrincipal) {
            KeycloakPrincipal<KeycloakSecurityContext> kPrincipal = (KeycloakPrincipal<KeycloakSecurityContext>) principal;

            AccessToken token = kPrincipal.getKeycloakSecurityContext().getToken();

            if (token == null) {
                System.out.println("NULL");
                return null;
            }
            userIdByToken = token.getEmail();
            System.out.println("ID: " + token.getSubject() + ", name: " + token.getName() + ", emailVerified" + token.getEmailVerified());
        }

        return userIdByToken;
    }

    @GetMapping("date")
    public LocalDateTime getDate() {
        return LocalDateTime.now();
    }

}
