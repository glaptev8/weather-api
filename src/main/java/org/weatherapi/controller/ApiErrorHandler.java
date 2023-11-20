package org.weatherapi.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.weatherapi.dto.ErrorResponse;
import org.weatherapi.exception.AuthException;
import org.weatherapi.exception.RateLimiterException;
import org.weatherapi.exception.UnauthorizedException;
import org.weatherapi.security.ApiKeyFilter;

import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.jsonwebtoken.MalformedJwtException;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class ApiErrorHandler {

  private static final Logger logger = LoggerFactory.getLogger(ApiErrorHandler.class);


  @ExceptionHandler(AuthException.class)
  public Mono<ResponseEntity<ErrorResponse>> handleAuthException(AuthException e) {
    logger.info("AuthException was fetched: {0}", e);
    return Mono.just(new ResponseEntity<>(
      new ErrorResponse(e.getMessage(), e.getErrorCode()),
      HttpStatus.BAD_REQUEST
    ));
  }


  @ExceptionHandler(UnauthorizedException.class)
  public Mono<ResponseEntity<ErrorResponse>> handleAuthException(UnauthorizedException e) {
    logger.info("UnauthorizedException was fetched: {0}", e);
    return Mono.just(new ResponseEntity<>(
      new ErrorResponse(e.getMessage(), e.getErrorCode()),
      HttpStatus.UNAUTHORIZED
    ));
  }

  @ExceptionHandler(RequestNotPermitted.class)
  public Mono<ResponseEntity<ErrorResponse>> handleAuthException(RequestNotPermitted e) {
    logger.info("RateLimiterException was fetched: {0}", e);
    return Mono.just(new ResponseEntity<>(
      new ErrorResponse("Too many requests", "WEATHERAPI_TOO_MANY_REQUEST"),
      HttpStatus.TOO_MANY_REQUESTS
    ));
  }
}
