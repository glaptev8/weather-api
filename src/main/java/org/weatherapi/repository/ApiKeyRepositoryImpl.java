package org.weatherapi.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
public class ApiKeyRepositoryImpl implements ApiKeyRepository {

  private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;
  private final String API_KEY_REDIS_KEY = "apiKeys";

  public ApiKeyRepositoryImpl(@Qualifier("apiKeyTemplate") ReactiveRedisTemplate<String, String> reactiveRedisTemplate) {
    this.reactiveRedisTemplate = reactiveRedisTemplate;
  }

  @Override
  public Mono<Void> addApiKey(String apiKey) {
    return reactiveRedisTemplate.opsForList().leftPush(API_KEY_REDIS_KEY, apiKey).then(Mono.empty());
  }

  @Override
  public Mono<Boolean> removeApiKey(String apiKey) {
    return reactiveRedisTemplate.opsForList().remove(API_KEY_REDIS_KEY, 1, apiKey)
      .map(count -> count > 0);
  }

  @Override
  public Mono<Boolean> apiKeyExist(String apiKey) {
    return reactiveRedisTemplate.opsForList()
      .range(API_KEY_REDIS_KEY, 0, -1)
      .collectList()
      .map(list -> list != null && list.contains(apiKey))
      .log()
      .defaultIfEmpty(false);
  }
}
