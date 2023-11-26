package org.weatherapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.weatherapi.dto.StationDto;
import org.weatherapi.mapper.MapStructMapper;
import org.weatherapi.service.api.StationService;
import org.weatherapi.service.api.WeatherService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StationControllerImpl {

  private final StationService stationService;
  private final WeatherService weatherService;
  private final MapStructMapper mapper;

  @GetMapping("/api/stations")
  public Flux<StationDto> getStations() {
    log.info("api/stations call");
    return stationService.getAllStation()
      .map(mapper::stationToDto);
  }

  @GetMapping("/api/stations/{station_code}")
  public Mono<StationDto> getStationById(@PathVariable("station_code") String stationName) {
    return stationService.getInfoByStation(stationName)
      .flatMap(station -> weatherService.getWeatherByStation(station.getId())
        .map(mapper::weatherToDto)
        .collectList()
        .map(weathers -> {
          var stationDto = mapper.stationToDto(station);
          stationDto.setWeather(weathers);
          return stationDto;
        }));
  }
}