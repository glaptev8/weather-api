package org.weatherapi.dto;

public record ErrorResponse(String code,
                            String message) {
}
