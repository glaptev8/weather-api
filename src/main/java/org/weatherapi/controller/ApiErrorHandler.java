package org.weatherapi.controller;

import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.weatherapi.dto.ErrorResponse;
import org.weatherapi.exception.AuthException;
import org.weatherapi.exception.UnauthorizedException;
import io.jsonwebtoken.ExpiredJwtException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Order(-2)
@Component
@RequiredArgsConstructor
public class ApiErrorHandler implements ErrorWebExceptionHandler {

  private final ObjectMapper objectMapper;

  @Override
  public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
    var response = exchange.getResponse();
    response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
    if (ex instanceof UnauthorizedException e) {
      response.setStatusCode(HttpStatus.UNAUTHORIZED);
      return wrapResponse(response, e.getErrorCode(), e.getMessage(), HttpStatus.UNAUTHORIZED);
    }
    if (ex instanceof ExpiredJwtException e) {
      response.setStatusCode(HttpStatus.UNAUTHORIZED);
      return wrapResponse(response, "WEATHERAPI_UNAUTHORIZED", e.getMessage(), HttpStatus.UNAUTHORIZED);
    }
    if (ex instanceof RequestNotPermitted e) {
      response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
      return wrapResponse(response, "WEATHERAPI_TOO_MANY_REQUEST", e.getMessage(), HttpStatus.TOO_MANY_REQUESTS);
    }
    if (ex instanceof AuthException e) {
      response.setStatusCode(HttpStatus.BAD_REQUEST);
      return wrapResponse(response, e.getErrorCode(), e.getMessage(), HttpStatus.BAD_GATEWAY);
    }
    return null;
  }

  private Mono<Void> wrapResponse(ServerHttpResponse response, String errorCode, String errorMessage, HttpStatus httpStatus) {
    try {
      return response
        .writeWith(Mono.just(response
                               .bufferFactory()
                               .wrap(objectMapper
                                       .writeValueAsString(new ResponseEntity<>(ErrorResponse.builder()
                                                                                  .code(errorCode)
                                                                                  .message(errorMessage)
                                                                                  .build(),
                                                                                httpStatus))
                                       .getBytes())));
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
