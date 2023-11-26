package org.weatherapi.service;

import org.springframework.stereotype.Service;
import org.weatherapi.entity.Station;
import org.weatherapi.repository.StationPostgresRepository;
import org.weatherapi.repository.StationRedisRepository;
import org.weatherapi.service.api.StationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class StationServiceImpl implements StationService {

  private final StationRedisRepository stationRedisRepository;
  private final StationPostgresRepository stationPostgresRepository;
  @Override
  public Mono<Station> addStation(Station station) {
    log.info("saving station: {}", station);
    return stationPostgresRepository.save(station)
      .flatMap(stationRedisRepository::save);
  }

  @Override
  public Flux<Station> getAllStation() {
    log.info("get all stations");
    return stationRedisRepository.findAll();
  }

  @Override
  public Mono<Station> getInfoByStation(String stationName) {
    log.info("searching station by name: {}", stationName);
    return stationRedisRepository.findByStationName(stationName)
      .doOnSuccess(station -> log.info("station was found: {}", station))
      .doOnError(e -> log.info("station was not found: {0}", e));
  }
}
