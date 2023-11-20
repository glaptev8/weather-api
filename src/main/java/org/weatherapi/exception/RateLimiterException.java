package org.weatherapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.TOO_MANY_REQUESTS)
public class RateLimiterException extends ApiException {
  public RateLimiterException(String message) {
    super(message, "WEATHERAPI_TOO_MANY_REQUEST");
  }
}
