package org.weatherapi.repository;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.stereotype.Service;
import org.weatherapi.controller.ApiErrorHandler;
import org.weatherapi.entity.Station;

import org.springframework.data.redis.core.ReactiveRedisTemplate;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class StationRepositoryImpl implements StationRepository {
  private static final Logger logger = LoggerFactory.getLogger(StationRepositoryImpl.class);

  private static final String STATIONS_KEY = "stations";
  private final ReactiveHashOperations<String, String, Station> reactiveHashOperations;

  @Override
  public Flux<Station> findAll() {
    logger.info("get all stations");
    return reactiveHashOperations
      .values(STATIONS_KEY)
      .filter(Station::getActive)
      .doOnNext(station -> logger.info("station was found: {}", station));
  }

  @Override
  public Mono<Boolean> save(Station station) {
    station.setCreatedAt(LocalDateTime.now());
    logger.info("saving station: {}", station);
    return reactiveHashOperations
      .put(STATIONS_KEY, station.getStationName(), station);
  }

  @Override
  public Mono<Station> findByStationName(String stationName) {
    logger.info("searching station by name: {}", stationName);
    return reactiveHashOperations
      .get(STATIONS_KEY, stationName)
      .doOnSuccess(station -> logger.info("station was found: {}", station))
      .doOnError(e -> logger.info("station was not found: {0}", e));
  }
}
