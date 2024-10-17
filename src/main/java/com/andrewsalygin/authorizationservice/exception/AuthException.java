package com.andrewsalygin.authorizationservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AuthException extends RuntimeException {

    private final String description;

    private final HttpStatus status;

    public AuthException(String description, String message, HttpStatus status) {
        super(message);
        this.description = description;
        this.status = status;
    }
}
