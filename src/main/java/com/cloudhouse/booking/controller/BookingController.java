package com.cloudhouse.booking.controller;

import ch.qos.logback.classic.Logger;
import com.cloudhouse.booking.entity.Response;
import com.cloudhouse.booking.entity.booking.Booking;
import com.cloudhouse.booking.entity.booking.Room;
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

@RestController
@RequestMapping("booking")
public class BookingController {

    private RoomService roomService;

    private BookingService bookingService;

    private static final Logger logger = (Logger) LoggerFactory.getLogger(BookingController.class);

    @Autowired
    public BookingController(RoomService roomService, BookingService bookingService) {
        this.roomService = roomService;
        this.bookingService = bookingService;
    }

    @RolesAllowed({"admin","manager","guest"})
    @PostMapping("create")
    public Response<Booking> createBooking(@RequestBody Booking booking) {
        System.out.println(booking);
        return bookingService.createBooking(booking);
    }

    @RolesAllowed({"admin","manager","guest"})
    @GetMapping
    public Response<List<Booking>> getBooking() {
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
