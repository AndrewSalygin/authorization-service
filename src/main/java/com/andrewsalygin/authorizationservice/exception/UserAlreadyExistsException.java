package com.andrewsalygin.authorizationservice.exception;

import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends AuthException {

    public UserAlreadyExistsException(String description, String message, HttpStatus status) {
        super(description, message, status);
    }
}
