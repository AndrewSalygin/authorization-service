package com.andrewsalygin.authorizationservice.util;

import com.andrewsalygin.authorizationservice.domain.entity.UserEntity;

import com.andrewsalygin.authorizationservice.domain.model.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.time.ZonedDateTime;
import java.util.UUID;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JwtTokenUtil {

    private static final String ISSUER = "com.andrewsalygin.authorization-service";

    @Value("${secrets.jwt-secret}")
    private String SECRET_KEY;

    @SuppressWarnings("MagicNumber")
    public String generateToken(User user, Long minutesToExpire) {
        Date expirationDate = Date.from(ZonedDateTime.now().plusMinutes(minutesToExpire).toInstant());
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);

        return JWT.create()
                .withIssuer(ISSUER)
                .withSubject(user.username())
                .withClaim("id", user.id().toString())
                .withIssuedAt(new Date())
                .withExpiresAt(expirationDate)
                .sign(algorithm);
    }

    public boolean validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
            JWTVerifier verifier = JWT.require(algorithm).build();
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT jwt = verifier.verify(token);
        return jwt.getSubject();
    }

    public UUID getIdFromToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT jwt = verifier.verify(token);

        return UUID.fromString(jwt.getClaim("id").asString());
    }
}
