package org.weatherapi.service.api;

import reactor.core.publisher.Flux;

public interface WeatherGeneratorService {
  Flux<Void> generateStationAndWeather();
}
