package com.tms.app.services.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tms.app.dtos.auth.request.AuthenticationRequest;
import com.tms.app.dtos.auth.request.SignupRequest;
import com.tms.app.dtos.auth.response.AuthenticationResponse;

public interface AuthenticationService {

    AuthenticationResponse login(AuthenticationRequest authenticationRequest) throws JsonProcessingException;

    AuthenticationResponse signupUser(SignupRequest signupRequest) throws JsonProcessingException;

    AuthenticationResponse signupAdmin(SignupRequest signupRequest) throws JsonProcessingException;
}