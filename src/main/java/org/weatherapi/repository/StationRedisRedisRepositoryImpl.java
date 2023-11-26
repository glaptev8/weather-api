package org.weatherapi.repository;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import org.weatherapi.entity.Station;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class StationRedisRedisRepositoryImpl implements StationRedisRepository {
  private static final String STATIONS_KEY = "station:";
  private final ReactiveRedisTemplate<String, Station> stationRedisTemplate;

  public StationRedisRedisRepositoryImpl(@Qualifier("stationTemplate") ReactiveRedisTemplate<String, Station> stationRedisTemplate) {
    this.stationRedisTemplate = stationRedisTemplate;
  }

  @Override
  public Flux<Station> findAll() {
    return stationRedisTemplate
      .keys(STATIONS_KEY + "*")
      .flatMap(stationRedisTemplate.opsForValue()::get);
  }

  @Override
  public Mono<Station> save(Station station) {
    station.setCreatedAt(LocalDateTime.now());
    return stationRedisTemplate
      .opsForValue()
      .set(STATIONS_KEY + station.getStationName(), station)
      .then(Mono.just(station));
  }

  @Override
  public Mono<Station> findByStationName(String stationName) {
    return stationRedisTemplate
      .opsForValue()
      .get(STATIONS_KEY + stationName);
  }
}
