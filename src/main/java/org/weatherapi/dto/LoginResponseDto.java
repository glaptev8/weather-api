package org.weatherapi.dto;

public record LoginResponseDto(String jwtToken,
                               String userName) {
}