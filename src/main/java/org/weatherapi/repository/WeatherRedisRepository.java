package org.weatherapi.repository;

import org.weatherapi.entity.Weather;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface WeatherRedisRepository {
  Mono<Weather> save(Weather weather);

  Flux<Weather> findAllWeatherByStation(Long stationId);
}
