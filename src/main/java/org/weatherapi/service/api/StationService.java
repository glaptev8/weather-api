package org.weatherapi.service.api;

import org.weatherapi.entity.Station;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StationService {

  Mono<Station> addStation(Station station);
  Flux<Station> getAllStation();
  Mono<Station> getInfoByStation(String stationName);
}
