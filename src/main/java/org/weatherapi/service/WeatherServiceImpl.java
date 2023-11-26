package org.weatherapi.service;

import org.springframework.stereotype.Service;
import org.weatherapi.entity.Weather;
import org.weatherapi.repository.WeatherPostgresRepository;
import org.weatherapi.repository.WeatherRedisRepository;
import org.weatherapi.service.api.WeatherService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherServiceImpl implements WeatherService {

  private final WeatherPostgresRepository weatherPostgresRepository;
  private final WeatherRedisRepository weatherRedisRepository;

  @Override
  public Mono<Weather> addWeather(Weather weather) {
    log.info("saving weather to redis: {}", weather);
    return weatherPostgresRepository
      .save(weather)
      .flatMap(weatherRedisRepository::save);
  }

  @Override
  public Flux<Weather> getWeatherByStation(Long stationId) {
    return weatherRedisRepository.findAllWeatherByStation(stationId)
      .doOnNext(weather -> log.info("weather was find: {}", weather))
      .switchIfEmpty(Flux.defer(() -> {
        log.info("stationId {} does not contain any weather", stationId);
        return Flux.empty();
      }));
  }
}
