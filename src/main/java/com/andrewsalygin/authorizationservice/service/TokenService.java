package com.andrewsalygin.authorizationservice.service;

import com.andrewsalygin.authorizationservice.domain.model.User;
import com.andrewsalygin.authorizationservice.util.JwtTokenUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.web.authentication.rememberme.InvalidCookieException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenService {

    private static final String ACCESS_TOKEN_NAME = "Access-Token";
    private static final String REFRESH_TOKEN_NAME = "Refresh-Token";

    private final JwtTokenUtil jwtTokenUtil;

    @Value("${security.access-token.expirationMillis}")
    private Long accessTokenExpirationMillis;

    @Value("${security.refresh-token.expirationMillis}")
    private Long refreshTokenExpirationMillis;

    public void addAuthCookiesToResponse(User user, HttpServletResponse response) {
        String accessToken = jwtTokenUtil.generateToken(user, accessTokenExpirationMillis);
        String refreshToken = jwtTokenUtil.generateToken(user, refreshTokenExpirationMillis);

        addCookie(response, ACCESS_TOKEN_NAME, accessToken, null, accessTokenExpirationMillis);
        addCookie(response, REFRESH_TOKEN_NAME, refreshToken, "/refresh", refreshTokenExpirationMillis);
    }

    public void refreshTokens(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = getCookieValue(request)
                .orElseThrow(() -> new InvalidCookieException("Refresh token не найден."));

        if (!jwtTokenUtil.validateToken(refreshToken)) {
            throw new InvalidCookieException("Refresh token неверный.");
        }

        UUID id = jwtTokenUtil.getIdFromToken(refreshToken);
        String username = jwtTokenUtil.getUsernameFromToken(refreshToken);
        User user = new User(id, username);
        addAuthCookiesToResponse(user, response);
    }

    private void addCookie(HttpServletResponse response, String name, String value, String path, long expirationMillis) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setMaxAge((int) expirationMillis / 1000);
        if (path != null) {
            cookie.setPath(path);
        }

        response.addCookie(cookie);
    }

    private Optional<String> getCookieValue(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return Optional.empty();
        }

        return Arrays.stream(cookies)
                .filter(cookie -> REFRESH_TOKEN_NAME.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }
}
