package com.tms.app.dtos.auth.response;

import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Response object for successful user signup")
public class SignupResponse {

    @Schema(description = "Unique identifier of the user", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @Schema(description = "Username chosen by the user", example = "johndoe")
    private String username;

    @Schema(description = "Email address of the user", example = "john.doe@example.com")
    private String email;

    @Schema(description = "Role assigned to the user", example = "USER")
    private String role;

    @Override
    public String toString() {
        return "SignupResponse{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}