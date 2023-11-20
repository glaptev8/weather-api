package org.weatherapi.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.weatherapi.dto.StationDto;
import org.weatherapi.dto.VerificationResult;
import org.weatherapi.dto.WeatherDto;
import org.weatherapi.entity.Station;
import org.weatherapi.entity.Weather;
import org.weatherapi.job.WeatherGeneratorJob;
import org.weatherapi.mapper.MapStructMapper;
import org.weatherapi.repository.StationRepository;
import org.weatherapi.service.StationServiceImpl;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StationServiceTest {
  @Mock
  private StationRepository stationRepository;
  @Mock
  private MapStructMapper mapper;
  @InjectMocks
  private StationServiceImpl stationService;

  @Test
  public void addStationTest() {
    final var stationName = UUID.randomUUID().toString();
    final var active = true;
    final var weather = new ArrayList<Weather>();
    final var weatherDtoResult = new ArrayList<WeatherDto>();
    final var station = new Station(stationName, active, weather);
    final var createdAt = LocalDateTime.now();
    final var stationDtoResult = new StationDto(stationName, active, createdAt, weatherDtoResult);

    when(stationRepository.save(station)).thenReturn(Mono.just(true));
    when(mapper.stationToDto(station)).thenReturn(stationDtoResult);

    StepVerifier.create(stationService.addStation(station))
      .expectNextMatches(stationDto -> stationDto.equals(stationDtoResult))
      .verifyComplete();

    verify(stationRepository).save(station);
    verify(mapper).stationToDto(station);
  }

  @Test
  public void getAllStationTest() {
    final var stationName = UUID.randomUUID().toString();
    final var active = true;
    final var weather = new ArrayList<Weather>();
    final var weatherDtoResult = new ArrayList<WeatherDto>();
    final var station = new Station(stationName, active, weather);
    final var createdAt = LocalDateTime.now();
    final var stationDtoResult = new StationDto(stationName, active, createdAt, weatherDtoResult);

    when(stationRepository.findAll()).thenReturn(Flux.just(station));
    when(mapper.stationToDto(station)).thenReturn(stationDtoResult);

    StepVerifier.create(stationService.getAllStation())
      .expectNextMatches(stationResult -> stationResult.equals(stationDtoResult))
      .verifyComplete();

    verify(mapper).stationToDto(station);
  }

  @Test
  public void getInfoByStationTest() {
    final var stationName = UUID.randomUUID().toString();
    final var active = true;
    final var weather = new ArrayList<Weather>();
    final var station = new Station(stationName, active, weather);
    final var createdAt = LocalDateTime.now();
    final var weatherDtoResult = new ArrayList<WeatherDto>();
    final var stationDtoResult = new StationDto(stationName, active, createdAt, weatherDtoResult);

    when(stationRepository.findByStationName(stationName)).thenReturn(Mono.just(station));
    when(mapper.stationToDto(station)).thenReturn(stationDtoResult);

    StepVerifier.create(stationService.getInfoByStation(stationName))
      .expectNextMatches(stationResult -> stationResult.equals(stationDtoResult))
      .verifyComplete();

    verify(mapper).stationToDto(station);
  }

  @Test
  public void addWeatherToStationTest() {
    final var stations = List.of(WeatherGeneratorJob.generateRandomStation(), WeatherGeneratorJob.generateRandomStation());
    final var weather = WeatherGeneratorJob.generateRandomWeather(stations.stream().map(Station::getStationName).toList());

    var station = stations
      .stream()
      .filter(st -> st.getStationName().equals(weather.getStationName()))
      .findFirst()
      .get();

    when(stationRepository.findByStationName(weather.getStationName())).thenReturn(Mono.just(station));
    station.getWeather().add(weather);
    when(stationRepository.save(station)).thenReturn(Mono.empty());

    StepVerifier.create(stationService.addWeatherToStation(weather))
      .expectNextCount(0)
      .verifyComplete();
  }
}
