package com.tms.app.dtos.auth.response;

import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignupResponse {

    private UUID id;
    private String username;
    private String email;
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