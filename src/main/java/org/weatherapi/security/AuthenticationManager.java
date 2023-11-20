package org.weatherapi.security;

import java.security.Principal;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.weatherapi.exception.UnauthorizedException;
import org.weatherapi.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AuthenticationManager implements ReactiveAuthenticationManager {

  private final UserRepository userRepository;

  @Override
  public Mono<Authentication> authenticate(Authentication authentication) {
    var principal = (CustomPrincipal) authentication.getPrincipal();
    return userRepository
      .findById(principal.id())
      .switchIfEmpty(Mono.error(new UnauthorizedException("User not found")))
      .map(user -> authentication);
  }
}
