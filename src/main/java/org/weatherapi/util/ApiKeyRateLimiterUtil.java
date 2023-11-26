package org.weatherapi.util;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;

@Component
public class ApiKeyRateLimiterUtil {

  @Value("${ratelimiter.station_limiter.limit-for-period}")
  private Integer periodLimit;
  @Value("${ratelimiter.station_limiter.limit-refresh-period}")
  private String periodRefresh;
  private final ConcurrentHashMap<String, RateLimiter> apiKeyRateLimiters = new ConcurrentHashMap<>();

  private RateLimiter generateRateLimiter(String apiKey) {
    return RateLimiter
      .of(apiKey,
          RateLimiterConfig.custom()
            .limitForPeriod(periodLimit)
            .limitRefreshPeriod(Duration.parse(periodRefresh))
            .build());
  }
  public RateLimiter getRateLimiter(String apiKey) {
    return apiKeyRateLimiters.computeIfAbsent(apiKey, this::generateRateLimiter);
  }

  public Void deleteRateLimiterForOldKey(String apiKey) {
    apiKeyRateLimiters.remove(apiKey);

    return null;
  }
}
