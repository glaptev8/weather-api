package org.weatherapi.repository;

import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import org.weatherapi.entity.Weather;
import org.springframework.data.domain.Range;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class WeatherRedisRepositoryImpl implements WeatherRedisRepository {

  private static final String WEATHER_KEY = "weather:station:";
  private final ReactiveRedisTemplate<String, Weather> weatherRedisTemplate;

  public WeatherRedisRepositoryImpl(@Qualifier("weatherTemplate") ReactiveRedisTemplate<String, Weather> weatherRedisTemplate) {
    this.weatherRedisTemplate = weatherRedisTemplate;
  }

  @Override
  public Mono<Weather> save(Weather weather) {
    long timestamp = weather.getWeatherDate().toEpochSecond(ZoneOffset.UTC);
    return weatherRedisTemplate
      .opsForZSet()
      .add(WEATHER_KEY + weather.getStationId(), weather, timestamp)
      .then(Mono.just(weather));
  }

  @Override
  public Flux<Weather> findAllWeatherByStation(Long stationId) {
    return weatherRedisTemplate
      .opsForZSet()
      .range(WEATHER_KEY + stationId, Range.unbounded());
  }
}
