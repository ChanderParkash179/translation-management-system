package com.tms.app.controllers.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.tms.app.dtos.auth.request.AuthenticationRequest;
import com.tms.app.dtos.auth.request.SignupRequest;
import com.tms.app.dtos.auth.response.AuthenticationResponse;
import com.tms.app.dtos.auth.response.SignupResponse;
import com.tms.app.dtos.wrapper.APIResponse;
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
@Tag(name = "Authentication", description = "Endpoints for user authentication and registration")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    /**
     * Handles user authentication (login) requests.
     */
    @PostMapping("/login")
    @Operation(summary = "Authenticate a user", description = "Processes user login with credentials and returns authentication tokens")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully authenticated", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<APIResponse<AuthenticationResponse>> login(@Valid @RequestBody AuthenticationRequest authenticationRequest) {
        AuthenticationResponse authenticationResponse = this.authenticationService.login(authenticationRequest);
        return ResponseEntity.status(HttpStatus.OK).body(
                APIResponse.success(HttpStatus.OK.value(), Message.LOGIN_SUCCESS.getMessage(), authenticationResponse));
    }

    /**
     * Handles user registration (signup) requests.
     */
    @PostMapping("/signup")
    @Operation(summary = "Signup a new user", description = "Creates a new user account and returns registration details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully registered", content = @Content(mediaType = "application/json", schema = @Schema(implementation = APIResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "409", description = "User already exists", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<APIResponse<SignupResponse>> signup(@Valid @RequestBody SignupRequest signupRequest) {
        SignupResponse authenticationResponse = this.authenticationService.signup(signupRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(APIResponse.success(HttpStatus.CREATED.value(), Message.SIGNUP_SUCCESS.getMessage(), authenticationResponse));
    }
}