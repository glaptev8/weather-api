package org.weatherapi.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.weatherapi.controller.api.AuthController;
import org.weatherapi.dto.UserDto;
import org.weatherapi.entity.User;
import org.weatherapi.security.CustomPrincipal;
import org.weatherapi.security.SecurityService;
import org.weatherapi.security.TokenDetails;
import org.weatherapi.service.api.UserService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class AuthControllerImpl implements AuthController {

  private final UserService userService;
  private final SecurityService securityService;

  @Override
  public Mono<UserDto> register(User user) {
    return userService.saveUser(user);
  }

  @Override
  public Mono<String> login(User user) {
    return securityService.authentication(user.getName(), user.getPassword())
      .map(TokenDetails::getToken);
  }

  @Override
  public Mono<String> getApiKey(Authentication authentication) {
    var principal = (CustomPrincipal)authentication.getPrincipal();
    return userService.generateNewApiKey(principal.id());
  }

  @GetMapping("/api/test")
  public Mono<String> qq() {
    return Mono.just("sadsadsad");
  }
}
