package com.tms.app.exceptions;

public class AccountNonActiveException extends RuntimeException{

    public AccountNonActiveException(String message) {
        super(message);
    }

    public AccountNonActiveException(String message, Throwable cause) {
        super(message, cause);
    }
}
