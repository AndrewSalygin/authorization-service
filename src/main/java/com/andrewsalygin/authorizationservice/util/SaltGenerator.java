package com.andrewsalygin.authorizationservice.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Arrays;

@Component
public class SaltGenerator {

    private final int saltLength;

    public SaltGenerator() {
        saltLength = 16;
    }

    public SaltGenerator(int saltLength) {
        this.saltLength = saltLength;
    }

    public String generateSalt() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] saltBytes = new byte[saltLength];
        secureRandom.nextBytes(saltBytes);

        return Arrays.toString(saltBytes);
    }
}
