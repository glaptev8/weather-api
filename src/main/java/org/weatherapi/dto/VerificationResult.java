package org.weatherapi.dto;

import io.jsonwebtoken.Claims;

public record VerificationResult(Claims claims,
                                 String token) {
}
