package com.tms.app.services.auth.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tms.app.dtos.auth.request.AuthenticationRequest;
import com.tms.app.dtos.auth.request.SignupRequest;
import com.tms.app.dtos.auth.response.AuthenticationResponse;
import com.tms.app.entities.role.Role;
import com.tms.app.entities.user.User;
import com.tms.app.enums.Message;
import com.tms.app.enums.RoleType;
import com.tms.app.exceptions.AccountNonActiveException;
import com.tms.app.exceptions.AlreadyExistsException;
import com.tms.app.repositories.role.RoleRepository;
import com.tms.app.repositories.user.UserRepository;
import com.tms.app.security.JWTService;
import com.tms.app.services.auth.AuthenticationService;
import com.tms.app.services.redis.RedisService;
import com.tms.app.services.token.UserSessionService;
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

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AppLogger log = new AppLogger(AuthenticationServiceImpl.class);

    private final JWTService jwtService;
    private final RedisService redisService;
    private final PasswordEncoder passwordEncoder;
    private final UserSessionService userSessionService;
    private final AuthenticationManager authenticationManager;

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Override
    public AuthenticationResponse login(AuthenticationRequest authenticationRequest) throws JsonProcessingException {
        String cacheKey = AppConstants.LOGIN_CACHE_PREFIX + authenticationRequest.getUsername();
        String cachedResponse = this.redisService.getData(cacheKey);
        if (cachedResponse != null) {
            log.info("Returning cached login response for user: {}", authenticationRequest.getUsername());
            return new ObjectMapper().readValue(cachedResponse, AuthenticationResponse.class);
        }

        log.info("Authenticating User");
        Optional<User> optionalUser = userRepository.findActiveUserByEmailOrUsername(authenticationRequest.getUsername());
        if (optionalUser.isEmpty()) {
            throw new BadCredentialsException(Message.INVALID_CREDENTIALS.getMessage());
        }

        User user = optionalUser.get();
        if (!user.getIsActive()) {
            throw new AccountNonActiveException("Account not Exists");
        }

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));
        var jwt = jwtService.generateToken(user);
        var refreshToken = this.jwtService.generateRefreshToken(new HashMap<>(), user);
        userSessionService.saveUserSession(jwt, user);

        AuthenticationResponse authenticationResponse = AuthenticationResponse.builder()
                .token(jwt)
                .refreshToken(refreshToken)
                .role(user.getRole().getRoleName())
                .build();

        redisService.saveData(cacheKey, new ObjectMapper().writeValueAsString(authenticationResponse));
        log.info("User Authenticated Successfully");

        return authenticationResponse;
    }

    @Override
    public AuthenticationResponse signupAdmin(SignupRequest signupRequest) throws JsonProcessingException {
        return signupExtract(signupRequest, RoleType.ADMIN);
    }

    @Override
    public AuthenticationResponse signupUser(SignupRequest signupRequest) throws JsonProcessingException {
        return signupExtract(signupRequest, RoleType.USER);
    }

    private AuthenticationResponse signupExtract(SignupRequest signupRequest, RoleType roleType) throws JsonProcessingException {
        String cacheKey = AppConstants.SIGNUP_CACHE_PREFIX + signupRequest.getEmail();
        String cachedResponse = redisService.getData(cacheKey);
        if (cachedResponse != null) {
            log.info("Returning cached signup response for email: {}", signupRequest.getEmail());
            return new ObjectMapper().readValue(cachedResponse, AuthenticationResponse.class);
        }

        Optional<User> optionalUser = userRepository.findActiveUserByEmail(signupRequest.getEmail());
        if (optionalUser.isPresent()) {
            throw new AlreadyExistsException(Message.DUPLICATE_EMAIL.getMessage());
        }

        User user = new User();
        user.setUsername(CustomUtils.generateUsername(signupRequest.getUsername()));
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setIsActive(Boolean.TRUE);
        user.setCreatedAt(LocalDateTime.now());

        Optional<Role> optionalRole = roleRepository.findRoleByName(roleType.getRoleType());
        user.setRole(optionalRole.orElseThrow(() -> new RuntimeException("No Role Found")));

        User savedUser = userRepository.save(user);
        userRepository.save(savedUser);

        var jwt = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(new HashMap<>(), user);
        userSessionService.saveUserSession(jwt, user);

        AuthenticationResponse authenticationResponse = AuthenticationResponse.builder()
                .token(jwt)
                .refreshToken(refreshToken)
                .role(user.getRole().getRoleName())
                .build();

        redisService.saveData(cacheKey, new ObjectMapper().writeValueAsString(authenticationResponse));
        log.info("User Registered Successfully");

        return authenticationResponse;
    }
}