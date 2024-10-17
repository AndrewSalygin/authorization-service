package com.andrewsalygin.authorizationservice.controller;

import com.andrewsalygin.authorizationservice.domain.dto.ApiErrorResponse;
import com.andrewsalygin.authorizationservice.exception.UserAlreadyExistsException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Arrays;

@RestControllerAdvice
public class ApplicationExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        return handleIncorrectRequest(ex, status);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        return handleIncorrectRequest(ex, status);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleCompanyException(UserAlreadyExistsException ex) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
                ex.getDescription(),
                String.valueOf(HttpStatus.BAD_REQUEST),
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                Arrays.stream(ex.getStackTrace()).map(StackTraceElement::toString).toList()
        );

        return new ResponseEntity<>(apiErrorResponse, ex.getStatus());
    }

    private ResponseEntity<Object> handleIncorrectRequest(
            Exception ex,
            HttpStatusCode status
    ) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
                "Запрос с неправильными параметрами",
                String.valueOf(status.value()),
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                Arrays.stream(ex.getStackTrace()).map(StackTraceElement::toString).toList()
        );

        return new ResponseEntity<>(apiErrorResponse, status);
    }
}
