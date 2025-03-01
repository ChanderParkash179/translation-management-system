package com.tms.app.dtos.auth.request;

import lombok.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request object for user signup")
public class SignupRequest {

    @NotEmpty(message = "Full Name cannot be Empty")
    @Schema(description = "Full name of the user", example = "John Doe")
    private String fullName;

    @NotEmpty(message = "Name cannot be Empty")
    @Schema(description = "Username chosen by the user", example = "johndoe")
    private String username;

    @NotEmpty(message = "Email cannot be Empty")
    @Email(message = "Please provide a valid email address")
    @Schema(description = "Email address of the user", example = "john.doe@example.com")
    private String email;

    @NotEmpty(message = "Password cannot be Empty")
    @Schema(description = "Password for the user account", example = "password123@")
    private String password;

    @Override
    public String toString() {
        int emailLength = email.length();
        int usernameLength = username.length();

        String maskedEmail = maskString(email, emailLength);
        String maskedUsername = maskString(username, usernameLength);
        int passwordLength = password.length();
        String maskedPassword = maskString(password, passwordLength);

        return "SignupRequest{" +
                " fullName='" + fullName + '\'' +
                ", username='" + maskedUsername + '\'' +
                ", email='" + maskedEmail + '\'' +
                ", password='" + maskedPassword + '\'' +
                '}';
    }

    private String maskString(String value, int length) {
        return value.substring(0, 2) + "*".repeat(length - 4) + value.substring(length - 2);
    }
}