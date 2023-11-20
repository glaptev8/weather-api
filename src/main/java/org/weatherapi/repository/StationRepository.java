package org.weatherapi.repository;

import org.weatherapi.entity.Station;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StationRepository {
  Flux<Station> findAll();
  Mono<Boolean> save(Station station);
  Mono<Station> findByStationName(String stationName);
}
