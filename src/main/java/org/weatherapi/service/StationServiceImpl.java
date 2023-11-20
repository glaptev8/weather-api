package org.weatherapi.service;

import org.springframework.stereotype.Service;
import org.weatherapi.dto.StationDto;
import org.weatherapi.entity.Station;
import org.weatherapi.entity.Weather;
import org.weatherapi.mapper.MapStructMapper;
import org.weatherapi.repository.StationRepository;
import org.weatherapi.service.api.StationService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class StationServiceImpl implements StationService {

  private final StationRepository stationRepository;
  private final MapStructMapper mapper;

  @Override
  public Mono<StationDto> addStation(Station station) {
    return stationRepository.save(station)
      .map(result -> mapper.stationToDto(station));
  }

  @Override
  public Flux<StationDto> getAllStation() {
    return stationRepository.findAll()
      .map(mapper::stationToDto);
  }

  @Override
  public Mono<StationDto> getInfoByStation(String stationName) {
    return stationRepository.findByStationName(stationName)
      .map(mapper::stationToDto);
  }

  @Override
  public Mono<Void> addWeatherToStation(Weather weather) {
    return stationRepository.findByStationName(weather.getStationName())
      .flatMap(station -> {
        station.getWeather().add(weather);
        return stationRepository.save(station).then();
      });
  }
}
