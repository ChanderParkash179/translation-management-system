package com.tms.app.dtos.auth.response;

import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Response object for successful user authentication")
public class AuthenticationResponse {

    @Schema(description = "JWT access token for authenticated user", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    @Schema(description = "Refresh token to obtain a new access token", example = "dGhpcy1pcy1hLXJlZnJlc2gtdG9rZW4...")
    private String refreshToken;

    @Schema(description = "Role assigned to the authenticated user", example = "USER")
    private String role;

    @Override
    public String toString() {
        return "AuthenticationResponse{" +
                "token='" + token + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}