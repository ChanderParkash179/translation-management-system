package com.tms.app.services.auth.impl;

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

        log.info("Authenticating User");
        User user = this.userRepository.findActiveUserByEmailOrUsername(authenticationRequest.getUsername())
                .orElseThrow(() -> {
                    log.warn(Message.INVALID_CREDENTIALS.getMessage());
                    return new BadCredentialsException(Message.INVALID_CREDENTIALS.getMessage());
                });

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));

        Object[] response = this.jwtTokenConcurrentCall(user);

        AuthenticationResponse authenticationResponse = AuthenticationResponse.builder()
                .token((String) response[0])
                .refreshToken((String) response[1])
                .role(user.getRole().getRoleName())
                .build();

        log.info("User Authenticated Successfully");
        return authenticationResponse;
    }

    @Override
    public SignupResponse signup(SignupRequest signupRequest) {

        Optional<User> optionalUser = this.userRepository.findActiveUserByEmail(signupRequest.getEmail());
        if (optionalUser.isPresent()) {
            log.info(Message.DUPLICATE_EMAIL.getMessage());
            throw new AlreadyExistsException(Message.DUPLICATE_EMAIL.getMessage());
        }

        User user = User.builder()
                .fullName(signupRequest.getFullName())
                .username(CustomUtils.generateUsername(signupRequest.getUsername()))
                .email(signupRequest.getEmail())
                .password(passwordEncoder.encode(signupRequest.getPassword()))
                .isActive(Boolean.TRUE)
                .role(this.roleRepository.findRoleByName(RoleType.USER.getRoleType()).orElse(null))
                .build();

        log.info("saving user");
        this.userRepository.save(user);

        return SignupResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().getRoleName())
                .build();
    }

    private Object[] jwtTokenConcurrentCall(User user) {
        var jwt = jwtService.generateToken(user);
        var refreshToken = this.jwtService.generateRefreshToken(new HashMap<>(), user);
        CompletableFuture.runAsync(() -> {
            redisService.saveData(AppConstants.JWT_SESSION_PREFIX + jwt, "true", 30);
        });

        return new Object[]{jwt, refreshToken};
    }
}