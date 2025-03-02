package com.tms.app.services.auth;

import com.tms.app.dtos.auth.request.AuthenticationRequest;
import com.tms.app.dtos.auth.request.SignupRequest;
import com.tms.app.dtos.auth.response.AuthenticationResponse;
import com.tms.app.dtos.auth.response.SignupResponse;

public interface AuthenticationService {

    AuthenticationResponse login(AuthenticationRequest authenticationRequest);

    SignupResponse signup(SignupRequest signupRequest);
}