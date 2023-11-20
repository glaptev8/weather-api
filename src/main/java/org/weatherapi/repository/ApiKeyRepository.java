package org.weatherapi.repository;

import reactor.core.publisher.Mono;

public interface ApiKeyRepository {
  Mono<Void> addApiKey(String apiKey);

  Mono<Boolean> removeApiKey(String apiKey);

  Mono<Boolean> apiKeyExist(String apiKey);
}
