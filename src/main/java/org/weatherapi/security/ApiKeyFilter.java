package org.weatherapi.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.weatherapi.exception.UnauthorizedException;
import org.weatherapi.repository.ApiKeyRepository;
import org.weatherapi.util.ApiKeyRateLimiterUtil;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.reactor.ratelimiter.operator.RateLimiterOperator;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ApiKeyFilter implements WebFilter {

  private static final Logger logger = LoggerFactory.getLogger(ApiKeyFilter.class);

  private final ApiKeyRepository redisService;
  private final ApiKeyRateLimiterUtil apiKeyRateLimiterUtil;

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    var path = exchange.getRequest().getPath().value();
    return isExcludedEndpoint(path)
      .flatMap(isExcludedPath -> {
        if (!isExcludedPath) {
          String apiKey = exchange.getRequest().getHeaders().getFirst("X-API-KEY");
          return isValidApiKey(apiKey)
            .flatMap(isValidApiKey -> {
                logger.info("api key is: {}", apiKey);
                if (!isValidApiKey) {
                  return Mono.error(new UnauthorizedException("not valid apiKey"));
                }
                RateLimiter rateLimiter = apiKeyRateLimiterUtil.getRateLimiter(apiKey);
                return chain.filter(exchange)
                  .transformDeferred(RateLimiterOperator.of(rateLimiter));
            });
        }
        else {
          return chain.filter(exchange);
        }
      });
  }

  private Mono<Boolean> isExcludedEndpoint(String path) {
    return Mono.just(path.equals("/api/login") || path.equals("/api/register") || path.equals("/api/get-api-key"));
  }

  private Mono<Boolean> isValidApiKey(String apiKey) {
    logger.info("validation api key: {}", apiKey);
    return redisService.apiKeyExist(apiKey);
  }
}
