package org.weatherapi.controller.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.weatherapi.dto.StationDto;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StationController {
  @GetMapping("/api/stations")
  Flux<StationDto> getStations();

  @GetMapping("/api/stations/{station_code}")
  Mono<StationDto> getStationByName(@PathVariable("station_code")String stationName);
}
