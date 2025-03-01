package com.tms.app.dtos.auth.request;

import lombok.*;
import jakarta.validation.constraints.NotEmpty;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {

    @NotEmpty(message = "Username cannot be Empty")
    private String username;
    @NotEmpty(message = "Password cannot be Empty")
    private String password;

    @Override
    public String toString() {

        // Get the length of the password
        int emailLength = username.length();

        // Mask email middle characters with asterisks
        String maskedEmail = username.substring(0, 2) + "*".repeat(emailLength - 4) + username.substring(emailLength - 2);

        // Get the length of the password
        int passwordLength = password.length();

        // Mask password middle characters with asterisks
        String maskedPassword = password.substring(0, 2) + "*".repeat(passwordLength - 4) + password.substring(passwordLength - 2);

        return "AuthenticationRequest{" +
                "email='" + maskedEmail + '\'' +
                ", password='" + maskedPassword + '\'' +
                '}';
    }
}