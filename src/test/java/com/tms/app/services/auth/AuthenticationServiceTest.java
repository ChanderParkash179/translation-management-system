package com.tms.app.services.auth;

import com.tms.app.dtos.auth.request.AuthenticationRequest;
import com.tms.app.dtos.auth.request.SignupRequest;
import com.tms.app.dtos.auth.response.AuthenticationResponse;
import com.tms.app.entities.role.Role;
import com.tms.app.entities.user.User;
import com.tms.app.enums.RoleType;
import com.tms.app.exceptions.AlreadyExistsException;
import com.tms.app.repositories.role.RoleRepository;
import com.tms.app.repositories.user.UserRepository;
import com.tms.app.security.JWTService;
import com.tms.app.services.auth.impl.AuthenticationServiceImpl;
import com.tms.app.services.redis.RedisService;
import com.tms.app.utils.AppConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @Mock
    private JWTService jwtService;

    @Mock
    private RedisService redisService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @BeforeEach
    void setUp() {
        reset(jwtService, redisService, passwordEncoder, authenticationManager, roleRepository, userRepository);
    }

    // Tests for login(AuthenticationRequest request)
    @Test
    void login_success() {
        AuthenticationRequest request = new AuthenticationRequest("user", "pass");
        User user = User.builder().username("user").password("encoded").role(new Role(RoleType.USER.getRoleType())).build();
        when(userRepository.findActiveUserByEmailOrUsername("user")).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");
        when(jwtService.generateRefreshToken(any(HashMap.class), eq(user))).thenReturn("refresh-token");

        AuthenticationResponse response = authenticationService.login(request);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("refresh-token", response.getRefreshToken());
        assertEquals(RoleType.USER.getRoleType(), response.getRole());
        verify(redisService, times(1)).saveData(eq(AppConstants.JWT_SESSION_PREFIX + "jwt-token"), eq("true"), eq(30));
    }

    @Test
    void login_invalidCredentials_throwsException() {
        AuthenticationRequest request = new AuthenticationRequest("user", "pass");
        when(userRepository.findActiveUserByEmailOrUsername("user")).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, () -> authenticationService.login(request));
        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    void login_wrongPassword_throwsException() {
        AuthenticationRequest request = new AuthenticationRequest("user", "wrong");
        User user = User.builder().username("user").password("encoded").build();
        when(userRepository.findActiveUserByEmailOrUsername("user")).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid password"));

        assertThrows(BadCredentialsException.class, () -> authenticationService.login(request));
    }

    @Test
    void login_cacheJwt() {
        AuthenticationRequest request = new AuthenticationRequest("user", "pass");
        User user = User.builder().username("user").password("encoded").role(new Role(RoleType.USER.getRoleType())).build();
        when(userRepository.findActiveUserByEmailOrUsername("user")).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");
        when(jwtService.generateRefreshToken(any(HashMap.class), eq(user))).thenReturn("refresh-token");

        AuthenticationResponse response = authenticationService.login(request);

        assertNotNull(response);
        verify(redisService, times(1)).saveData(eq(AppConstants.JWT_SESSION_PREFIX + "jwt-token"), eq("true"), eq(30));
    }

    @Test
    void signup_duplicateEmail_throwsException() {
        SignupRequest request = new SignupRequest("John Doe", "john", "john@example.com", "pass");
        when(userRepository.findActiveUserByEmail("john@example.com")).thenReturn(Optional.of(new User()));

        assertThrows(AlreadyExistsException.class, () -> authenticationService.signup(request));
        verify(userRepository, never()).save(any(User.class));
    }
}