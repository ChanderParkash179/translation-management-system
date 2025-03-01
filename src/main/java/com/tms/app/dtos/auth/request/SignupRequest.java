package com.tms.app.dtos.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequest {

    @NotEmpty(message = "Full Name cannot be Empty")
    private String fullName;

    @NotEmpty(message = "Name cannot be Empty")
    private String username;

    @NotEmpty(message = "Email cannot be Empty")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotEmpty(message = "Password cannot be Empty")
    private String password;

    @Override
    public String toString() {

        int emailLength = email.length();
        int usernameLength = username.length();

        // Mask email middle characters with asterisks
        String maskedEmail = maskString(email, emailLength);

        // Mask username middle characters with asterisks
        String maskedUsername = maskString(username, usernameLength);

        // Get the length of the password
        int passwordLength = password.length();

        // Mask password middle characters with asterisks
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