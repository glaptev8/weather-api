package org.weatherapi.service.api;

import org.weatherapi.dto.StationDto;
import org.weatherapi.entity.Station;
import org.weatherapi.entity.Weather;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StationService {

  Mono<StationDto> addStation(Station station);
  Flux<StationDto> getAllStation();

  Mono<StationDto> getInfoByStation(String stationName);

  Mono<Void> addWeatherToStation(Weather weather);
}
