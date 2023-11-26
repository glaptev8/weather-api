package org.weatherapi.repository;

import org.weatherapi.entity.Station;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StationRedisRepository {
  Flux<Station> findAll();
  Mono<Station> save(Station station);
  Mono<Station> findByStationName(String stationName);
}
