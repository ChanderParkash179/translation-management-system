package com.tms.app.exceptions.handler;

import com.tms.app.dtos.wrapper.ErrorResponse;
import com.tms.app.enums.Message;
import com.tms.app.exceptions.*;
import com.tms.app.utils.AppLogger;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.password.CompromisedPasswordException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final AppLogger log = new AppLogger(GlobalExceptionHandler.class);

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {

        log.info("Validation Error");

        Map<String, Object> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors()
                .forEach(error -> {
                    String fieldName = ((FieldError) error).getField();
                    String errorMessage = error.getDefaultMessage();
                    errors.put(fieldName, errorMessage);
                });

        log.info(errors.toString());

        return ResponseEntity.status(BAD_REQUEST).body(
                ErrorResponse.error(BAD_REQUEST.value(), Message.VALIDATION_ERROR.getMessage(), errors));
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleNonUniqueException(AlreadyExistsException ex) {

        log.info("Already Exists Error: {}", ex.getMessage());
        return ResponseEntity.status(BAD_REQUEST).body(ErrorResponse.error(BAD_REQUEST.value(), ex.getMessage()));
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException ex) {

        log.info("Bad Credentials Error: {}", ex.getMessage());
        return ResponseEntity.status(BAD_REQUEST)
                .body(ErrorResponse.error(BAD_REQUEST.value(), Message.INVALID_CREDENTIALS.getMessage()));
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ErrorResponse> handleDisabledException(DisabledException ex) {

        log.info("Disabled Error: " + ex.getMessage());
        return ResponseEntity.status(BAD_REQUEST).body(ErrorResponse.error(BAD_REQUEST.value(), ex.getMessage()));
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {

        log.info("Max Upload Size Exceeded Error: {}", ex.getMessage());
        return ResponseEntity.status(BAD_REQUEST)
                .body(ErrorResponse.error(BAD_REQUEST.value(), "File Size should not be more than 10MB"));
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException ex) {

        log.info("Bad Request Error: {}", ex.getMessage());
        return ResponseEntity.status(BAD_REQUEST).body(ErrorResponse.error(BAD_REQUEST.value(), ex.getMessage()));
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(AccountNonActiveException.class)
    public ResponseEntity<ErrorResponse> handleAccountNonActiveException(AccountNonActiveException ex) {

        log.info("Account Non Active Error: " + ex.getMessage());
        return ResponseEntity.status(BAD_REQUEST).body(ErrorResponse.error(BAD_REQUEST.value(), ex.getMessage()));
    }

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {

        log.info("Resource Not Found Error: {}", ex.getMessage());
        return ResponseEntity.status(NOT_FOUND).body(ErrorResponse.error(NOT_FOUND.value(), ex.getMessage()));
    }

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFoundException(NoResourceFoundException ex) {

        log.info("No Resource Found Error: {}", ex.getMessage());
        return ResponseEntity.status(NOT_FOUND)
                .body(ErrorResponse.error(NOT_FOUND.value(), Message.RESOURCE_NOT_FOUND.getMessage()));
    }

    @ResponseStatus(UNAUTHORIZED)
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex) {

        log.info("Authentication Error: {}", ex.getMessage());
        return ResponseEntity.status(UNAUTHORIZED)
                .body(ErrorResponse.error(UNAUTHORIZED.value(), Message.AUTHENTICATION_REQUIRED.getMessage()));
    }

    @ResponseStatus(UNAUTHORIZED)
    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ErrorResponse> handleTokenExpiredException(TokenExpiredException ex) {

        log.info("Token Expired Error: {}", ex.getMessage());
        return ResponseEntity.status(UNAUTHORIZED)
                .body(ErrorResponse.error(UNAUTHORIZED.value(), Message.SESSION_EXPIRED.getMessage()));
    }

    @ResponseStatus(UNAUTHORIZED)
    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<ErrorResponse> handleMalformedJwtException(MalformedJwtException ex) {

        log.info("Malformed Jwt Error: " + ex.getMessage());
        return ResponseEntity.status(UNAUTHORIZED)
                .body(ErrorResponse.error(UNAUTHORIZED.value(), Message.NOT_AUTHORIZED.getMessage()));
    }

    @ResponseStatus(UNAUTHORIZED)
    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<ErrorResponse> handleSignatureException(SignatureException ex) {

        log.info("JWT Signature Error: " + ex.getMessage());
        return ResponseEntity.status(UNAUTHORIZED)
                .body(ErrorResponse.error(UNAUTHORIZED.value(), Message.NOT_AUTHORIZED.getMessage()));
    }

    @ResponseStatus(UNAUTHORIZED)
    @ExceptionHandler(TokenMissingException.class)
    public ResponseEntity<ErrorResponse> handleTokenMissingException(TokenMissingException ex) {

        log.info("Nonce Token Missing Error: " + ex.getMessage());
        return ResponseEntity.status(UNAUTHORIZED)
                .body(ErrorResponse.error(UNAUTHORIZED.value(), Message.NOT_AUTHORIZED.getMessage()));
    }

    @ResponseStatus(FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {

        log.info("Access Denied Error: " + ex.getMessage());
        return ResponseEntity.status(FORBIDDEN)
                .body(ErrorResponse.error(FORBIDDEN.value(), Message.ACCESS_DENIED.getMessage()));
    }

    @ResponseStatus(UNAUTHORIZED)
    @ExceptionHandler(CompromisedPasswordException.class)
    public ResponseEntity<ErrorResponse>  handleCompromisedPasswordException(CompromisedPasswordException ex) {

        log.info("The provided password is compromised and cannot be used." + ex.getMessage());
        return ResponseEntity.status(UNAUTHORIZED)
                .body(ErrorResponse.error(UNAUTHORIZED.value(), Message.UNPROCESSABLE_ENTITY.getMessage()));
    }

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleInternalServerError(Exception ex) {

        log.error("Internal Server Error: {}", ex.getMessage());
        log.error("Error: ", ex.fillInStackTrace());
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(
                ErrorResponse.error(INTERNAL_SERVER_ERROR.value(), Message.INTERNAL_SERVER_ERROR.getMessage()));
    }
}
