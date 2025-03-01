package com.tms.app.services.auth.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tms.app.dtos.auth.request.AuthenticationRequest;
import com.tms.app.dtos.auth.request.SignupRequest;
import com.tms.app.dtos.auth.response.AuthenticationResponse;
import com.tms.app.dtos.auth.response.SignupResponse;
import com.tms.app.entities.user.User;
import com.tms.app.enums.Message;
import com.tms.app.enums.RoleType;
import com.tms.app.exceptions.AlreadyExistsException;
import com.tms.app.repositories.role.RoleRepository;
import com.tms.app.repositories.user.UserRepository;
import com.tms.app.security.JWTService;
import com.tms.app.services.auth.AuthenticationService;
import com.tms.app.services.redis.RedisService;
import com.tms.app.utils.AppConstants;
import com.tms.app.utils.AppLogger;
import com.tms.app.utils.CustomUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AppLogger log = new AppLogger(AuthenticationServiceImpl.class);

    private final JWTService jwtService;
    private final RedisService redisService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Override
    public AuthenticationResponse login(AuthenticationRequest authenticationRequest) {

        AuthenticationResponse cached = this.redisService.getCachedData(AppConstants.LOGIN_CACHE_PREFIX, authenticationRequest.getUsername(), "Returning cached login response for user", AuthenticationResponse.class);
        if (cached != null) return cached;

        log.info("Authenticating User");
        User user = userRepository.findActiveUserByEmailOrUsername(authenticationRequest.getUsername())
                .orElseThrow(() -> {
                    log.warn(Message.INVALID_CREDENTIALS.getMessage());
                    return new BadCredentialsException(Message.INVALID_CREDENTIALS.getMessage());
                });

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));

        var jwt = jwtService.generateToken(user);
        var refreshToken = this.jwtService.generateRefreshToken(new HashMap<>(), user);

        AuthenticationResponse authenticationResponse = AuthenticationResponse.builder()
                .token(jwt)
                .refreshToken(refreshToken)
                .role(user.getRole().getRoleName())
                .build();

        CompletableFuture.runAsync(() ->
        {
            try {
                this.redisService.saveData(AppConstants.LOGIN_CACHE_PREFIX + authenticationRequest.getUsername(), CustomUtils.writeAsJSON(authenticationResponse), 10);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
        log.info("User Authenticated Successfully");

        return authenticationResponse;
    }

    @Override
    public SignupResponse signup(SignupRequest signupRequest) {

        SignupResponse cached = this.redisService.getCachedData(AppConstants.SIGNUP_CACHE_PREFIX, signupRequest.getEmail(), "Returning cached signup response for email", SignupResponse.class);
        if (cached != null) return cached;

        Optional<User> optionalUser = this.userRepository.findActiveUserByEmail(signupRequest.getEmail());
        if (optionalUser.isPresent()) {
            throw new AlreadyExistsException(Message.DUPLICATE_EMAIL.getMessage());
        }

        User user = new User();
        user.setUsername(CustomUtils.generateUsername(signupRequest.getUsername()));
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setIsActive(Boolean.TRUE);
        user.setCreatedAt(LocalDateTime.now());

        user.setRole(this.roleRepository.findRoleByName(RoleType.USER.getRoleType()).orElse(null));

        this.userRepository.save(user);

        SignupResponse authenticationResponse = SignupResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().getRoleName())
                .build();

        CompletableFuture.runAsync(() -> {
            try {
                this.redisService.saveData(AppConstants.SIGNUP_CACHE_PREFIX + signupRequest.getEmail(), CustomUtils.writeAsJSON(authenticationResponse), 10);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
        log.info("User Registered Successfully");

        return authenticationResponse;
    }
}