package com.cloudhouse.booking.config;

import ch.qos.logback.classic.Logger;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
public class StaticDataLoader {

    private static final Logger logger = (Logger) LoggerFactory.getLogger(StaticDataLoader.class);

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
