package com.tms.app.exceptions;

public class PasswordException extends org.springframework.security.authentication.password.CompromisedPasswordException {

    public PasswordException(String message) {
        super(message);
    }


}
