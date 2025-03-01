package com.tms.app.security;

import com.tms.app.services.token.UserSessionService;
import com.tms.app.utils.AppLogger;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutHandlerService implements LogoutHandler {

    private final UserSessionService userSessionService;
    private final AppLogger log = new AppLogger(LogoutHandlerService.class);

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        final String jwtToken;
        final String bearer = "Bearer ";

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(authHeader != null && !authHeader.isEmpty() && StringUtils.startsWith(authHeader, bearer)) {

            log.info("Extracting Token from Header");
            jwtToken = authHeader.substring(bearer.length());
            log.info("Deleting User Session");
            userSessionService.deleteSession(jwtToken);
            log.info("Clearing Security Context");
            SecurityContextHolder.clearContext();
            log.info("User Logged Out Successfully");
        }
    }
}
