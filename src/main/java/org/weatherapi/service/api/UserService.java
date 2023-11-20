package org.weatherapi.service.api;

import org.weatherapi.dto.UserDto;
import org.weatherapi.entity.User;

import reactor.core.publisher.Mono;

public interface UserService {
  Mono<UserDto> saveUser(User user);

  Mono<String> generateNewApiKey(Long userId);
}
