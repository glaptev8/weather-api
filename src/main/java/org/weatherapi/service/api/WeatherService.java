package org.weatherapi.service.api;

import org.weatherapi.entity.Weather;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface WeatherService {
  Mono<Weather> addWeather(Weather weather);

  Flux<Weather> getWeatherByStation(Long stationId);
}
