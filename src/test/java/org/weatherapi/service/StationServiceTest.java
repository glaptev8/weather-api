package org.weatherapi.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.weatherapi.dto.StationDto;
import org.weatherapi.entity.Station;
import org.weatherapi.mapper.MapStructMapper;
import org.weatherapi.repository.StationPostgresRepository;
import org.weatherapi.repository.StationRedisRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StationServiceTest {
  @Mock
  private StationRedisRepository stationRedisRepository;
  @Mock
  private StationPostgresRepository stationPostgresRepository;
  @Mock
  private MapStructMapper mapper;
  @InjectMocks
  private StationServiceImpl stationService;

  @Test
  public void addStationTest() {
    final var stationName = UUID.randomUUID().toString();
    final var active = true;
    final var createdAt = LocalDateTime.now();
    final var station = Station.builder()
      .stationName(stationName)
      .active(active)
      .createdAt(createdAt)
      .build();

    when(stationRedisRepository.save(station)).thenReturn(Mono.just(station));
    when(stationPostgresRepository.save(station)).thenReturn(Mono.just(station));

    StepVerifier.create(stationService.addStation(station))
      .expectNextMatches(stationResult -> stationResult.equals(station))
      .verifyComplete();

    verify(stationRedisRepository).save(station);
  }

  @Test
  public void getAllStationTest() {
    final var stationName = UUID.randomUUID().toString();
    final var active = true;
    final var createdAt = LocalDateTime.now();
    final var station = Station.builder()
      .stationName(stationName)
      .active(active)
      .createdAt(createdAt)
      .build();

    when(stationRedisRepository.findAll()).thenReturn(Flux.just(station));

    StepVerifier.create(stationService.getAllStation())
      .expectNextMatches(stationResult -> stationResult.equals(station))
      .verifyComplete();
  }

  @Test
  public void getInfoByStationTest() {
    final var stationName = UUID.randomUUID().toString();
    final var active = true;
    final var createdAt = LocalDateTime.now();
    final var station = Station.builder()
      .stationName(stationName)
      .active(active)
      .createdAt(createdAt)
      .build();

    when(stationRedisRepository.findByStationName(stationName)).thenReturn(Mono.just(station));

    StepVerifier.create(stationService.getInfoByStation(stationName))
      .expectNextMatches(stationResult -> stationResult.equals(station))
      .verifyComplete();
  }
}
