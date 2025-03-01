package com.tms.app.exceptions;

public class TokenExpiredException extends RuntimeException {

    public TokenExpiredException(String message){
        super(message);
    }
}
