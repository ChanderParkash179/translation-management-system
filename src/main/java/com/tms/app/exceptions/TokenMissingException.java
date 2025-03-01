package com.tms.app.exceptions;

public class TokenMissingException extends RuntimeException {

    public TokenMissingException(String message){
        super(message);
    }
}
