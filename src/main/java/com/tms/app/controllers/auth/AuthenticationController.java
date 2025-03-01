package com.tms.app.controllers.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tms.app.dtos.auth.request.AuthenticationRequest;
import com.tms.app.dtos.auth.request.SignupRequest;
import com.tms.app.dtos.auth.response.AuthenticationResponse;
import com.tms.app.dtos.auth.response.SignupResponse;
import com.tms.app.dtos.wrapper.ApiResponse;
import com.tms.app.enums.Message;
import com.tms.app.services.auth.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> login(@Valid @RequestBody AuthenticationRequest authenticationRequest){

        AuthenticationResponse authenticationResponse = this.authenticationService.login(authenticationRequest);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(HttpStatus.OK.value(), Message.LOGIN_SUCCESS.getMessage(), authenticationResponse));
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponse>> signup(@Valid @RequestBody SignupRequest signupRequest){

        SignupResponse authenticationResponse = this.authenticationService.signup(signupRequest);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), Message.SIGNUP_SUCCESS.getMessage(), authenticationResponse));
    }
}