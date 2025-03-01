package com.tms.app.security;

import com.tms.app.entities.userSession.UserSession;
import com.tms.app.exceptions.TokenExpiredException;
import com.tms.app.services.token.UserSessionService;
import com.tms.app.utils.AppLogger;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import static com.tms.app.enums.Message.SESSION_EXPIRED;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final UserSessionService userSessionService;
    private final UserDetailsService userDetailsService;
    private final HandlerExceptionResolver resolver;

    private final AppLogger log = new AppLogger(JWTAuthenticationFilter.class);

    public JWTAuthenticationFilter(
            JWTService jwtService,
            UserSessionService userSessionService,
            UserDetailsService userDetailsService,
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {

        this.jwtService = jwtService;
        this.userSessionService = userSessionService;
        this.userDetailsService = userDetailsService;
        this.resolver = resolver;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) {
        try {

            String header = request.getHeader(HttpHeaders.AUTHORIZATION);

            final String jwt;
            final String userEmail;
            if (StringUtils.isEmpty(header) || !StringUtils.startsWith(header, "Bearer")) {
                filterChain.doFilter(request, response);
                return;
            }

            jwt = header.substring(7);
            userEmail = this.jwtService.extractUsername(jwt);

            if (!StringUtils.isEmpty(userEmail) && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
                boolean isTokenValid = userSessionService.findSessionByToken(jwt)
                        .map(UserSession::getIsActive)
                        .orElse(false);

                if (jwtService.isTokenValid(jwt, userDetails) && isTokenValid) {
                    SecurityContext context = SecurityContextHolder.createEmptyContext();
                    UsernamePasswordAuthenticationToken token =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    context.setAuthentication(token);
                    SecurityContextHolder.setContext(context);
                } else {
                    throw new TokenExpiredException(SESSION_EXPIRED.getMessage());
                }
            }

            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException ex) {
            log.info("Token Expired");
            resolver.resolveException(request, response, null, new TokenExpiredException(SESSION_EXPIRED.getMessage()));
        } catch (MalformedJwtException ex) {
            log.info("Invalid Token");
            resolver.resolveException(request, response, null, ex);
        } catch (SignatureException ex) {
            log.info("Invalid Token Signature");
            resolver.resolveException(request, response, null, ex);
        } catch (Exception ex) {
            resolver.resolveException(request, response, null, ex);
        }
    }
}