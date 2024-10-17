package com.andrewsalygin.authorizationservice.domain.model;

import java.util.UUID;

public record User(UUID id, String username) {}
