package com.tms.app.dtos.auth.request;

import lombok.*;
import jakarta.validation.constraints.NotEmpty;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request object for user authentication")
public class AuthenticationRequest {

    @NotEmpty(message = "Username cannot be Empty")
    @Schema(description = "Username of the user attempting to authenticate", example = "johndoe")
    private String username;

    @NotEmpty(message = "Password cannot be Empty")
    @Schema(description = "Password of the user attempting to authenticate", example = "Pass123@")
    private String password;

    @Override
    public String toString() {
        int emailLength = username.length();
        String maskedEmail = username.substring(0, 2) + "*".repeat(emailLength - 4) + username.substring(emailLength - 2);

        int passwordLength = password.length();
        String maskedPassword = password.substring(0, 2) + "*".repeat(passwordLength - 4) + password.substring(passwordLength - 2);

        return "AuthenticationRequest{" +
                "email='" + maskedEmail + '\'' +
                ", password='" + maskedPassword + '\'' +
                '}';
    }
}