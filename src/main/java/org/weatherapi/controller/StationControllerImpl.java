package org.weatherapi.controller;

import org.springframework.web.bind.annotation.RestController;
import org.weatherapi.controller.api.StationController;
import org.weatherapi.dto.StationDto;
import org.weatherapi.service.api.StationService;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RateLimiter(name = "station_limiter")
public class StationControllerImpl implements StationController {

  private final StationService stationService;

  @Override
  @RateLimiter(name = "stations_limiter")
  public Flux<StationDto> getStations() {
    return stationService.getAllStation();
  }

  @Override
  @RateLimiter(name = "station_limiter")
  public Mono<StationDto> getStationByName(String stationName) {
    return stationService.getInfoByStation(stationName);
  }
}