package com.tms.app.security;

import com.tms.app.services.redis.RedisService;
import com.tms.app.utils.AppConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogoutHandlerService implements LogoutHandler {

    private final RedisService redisService;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        final String jwtToken;
        final String bearer = "Bearer ";

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(authHeader != null && !authHeader.isEmpty() && StringUtils.startsWith(authHeader, bearer)) {
            log.info("Extracting Token from Header");
            jwtToken = authHeader.substring(bearer.length());
            log.info("Deleting User Session");
            this.redisService.deleteData(AppConstants.JWT_SESSION_PREFIX + jwtToken);
            log.info("Clearing Security Context");
            SecurityContextHolder.clearContext();
            log.info("User Logged Out Successfully");
        }
    }
}
