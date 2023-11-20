package org.weatherapi.controller.api;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.weatherapi.dto.UserDto;
import org.weatherapi.entity.User;

import reactor.core.publisher.Mono;

public interface AuthController {

  @PostMapping("/api/register")
  Mono<UserDto> register(@RequestBody User user);

  @PostMapping("/api/login")
  Mono<String> login(@RequestBody User user);

  @PostMapping("/api/get-api-key")
  Mono<String> getApiKey(Authentication authentication);
}
