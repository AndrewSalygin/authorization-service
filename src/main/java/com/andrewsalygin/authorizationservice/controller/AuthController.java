package com.andrewsalygin.authorizationservice.controller;

import com.andrewsalygin.authorizationservice.domain.dto.AuthUserRequest;
import com.andrewsalygin.authorizationservice.service.AuthService;
import com.andrewsalygin.authorizationservice.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private final TokenService tokenService;

    @PostMapping("/sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    public void signUp(@RequestBody AuthUserRequest authUserRequest) {
        authService.registerUser(authUserRequest.usr(), authUserRequest.pwd());
    }

    @PostMapping("/auth")
    @ResponseStatus(HttpStatus.OK)
    public void auth(@RequestBody AuthUserRequest authUserRequest, HttpServletResponse response) {
        authService.authenticateUser(authUserRequest.usr(), authUserRequest.pwd(), response);
    }

    @PostMapping("/refresh")
    @ResponseStatus(HttpStatus.OK)
    public void refresh(HttpServletRequest request, HttpServletResponse response) {
        tokenService.refreshTokens(request, response);
    }
}
