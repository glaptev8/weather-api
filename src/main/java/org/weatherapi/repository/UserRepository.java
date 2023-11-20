package org.weatherapi.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.weatherapi.entity.User;

import reactor.core.publisher.Mono;

public interface UserRepository extends R2dbcRepository<User, Long> {
  Mono<User> findByName(String name);

  @Query("Select u.api_key from weather_user u where u.id = :userId")
  Mono<String> getApiKeyByUserId(Long userId);

  @Query("Update weather_user set api_key = :apiKey where id = :userId")
  Mono<Void> updateApiKey(String apiKey, Long userId);
}
