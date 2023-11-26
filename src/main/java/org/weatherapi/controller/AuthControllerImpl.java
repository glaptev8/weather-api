package org.weatherapi.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.weatherapi.dto.ApiKeyResponseDto;
import org.weatherapi.dto.LoginResponseDto;
import org.weatherapi.dto.UserDto;
import org.weatherapi.entity.User;
import org.weatherapi.security.CustomPrincipal;
import org.weatherapi.security.SecurityService;
import org.weatherapi.service.api.UserService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class AuthControllerImpl {

  private final UserService userService;
  private final SecurityService securityService;

  @PostMapping("/api/register")
  public Mono<UserDto> register(@RequestBody User user) {
    return userService.saveUser(user);
  }
  @PostMapping("/api/login")
  public Mono<LoginResponseDto> login(@RequestBody User user) {
    return securityService.authentication(user.getName(), user.getPassword())
      .map(tokenDetails -> new LoginResponseDto(tokenDetails.getToken(), user.getName()));
  }

  @PostMapping("/api/get-api-key")
  public Mono<ApiKeyResponseDto> getApiKey(Authentication authentication) {
    var principal = (CustomPrincipal) authentication.getPrincipal();
    return userService.generateNewApiKey(principal.id())
      .flatMap(apiKey -> Mono.just(new ApiKeyResponseDto(apiKey)));
  }
}
