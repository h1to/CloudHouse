package com.cloudhouse.booking.config;

import ch.qos.logback.classic.Logger;
import com.cloudhouse.booking.entity.booking.BookingStatus;
import com.cloudhouse.booking.repository.BookingStatusRepo;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.List;

@Component
public class StaticDataLoader {

    private BookingStatusRepo bookingStatusRepo;

    public static List<BookingStatus> bookingStatuses;
    private static final Logger logger = (Logger) LoggerFactory.getLogger(StaticDataLoader.class);

    @Autowired
    public StaticDataLoader(BookingStatusRepo bookingStatusRepo) {
        this.bookingStatusRepo = bookingStatusRepo;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void getAllStatuses() {
        bookingStatuses = bookingStatusRepo.findAll();
        logger.info("Booking statuses loaded successfully");
    }

    public static String getUserId() {
        String userID = null;
        KeycloakAuthenticationToken authentication =
                (KeycloakAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        Principal principal = authentication.getAccount().getPrincipal();

        if (principal instanceof KeycloakPrincipal) {
            KeycloakPrincipal<KeycloakSecurityContext> kPrincipal = (KeycloakPrincipal<KeycloakSecurityContext>) principal;

            AccessToken token = kPrincipal.getKeycloakSecurityContext().getToken();
            userID = token.getSubject();
        }
        return userID;
    }

}
