package org.weatherapi.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Random;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.weatherapi.dto.CloudType;
import org.weatherapi.dto.PrecipitationType;
import org.weatherapi.dto.WindDurationType;
import org.weatherapi.entity.Station;
import org.weatherapi.entity.Weather;
import org.weatherapi.service.api.StationService;
import org.weatherapi.service.api.WeatherGeneratorService;
import org.weatherapi.service.api.WeatherService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class WeatherGeneratorServiceImpl implements WeatherGeneratorService {
  private final Random random = new Random();
  private final StationService stationService;
  private final WeatherService weatherService;

  @Override
  public Flux<Void> generateStationAndWeather() {
    return Flux.range(0, random.nextInt(3))
      .flatMap(index -> Mono.just(generateStation()))
      .flatMap(stationService::addStation)
      .thenMany(stationService.getAllStation()
                  .collectList()
                  .flatMapMany(stations -> Flux.range(0, random.nextInt(6))
                    .flatMap(index -> Mono.just(generateWeather(stations.get(random.nextInt(stations.size())).getId())))
                    .flatMap(weather -> weatherService
                      .addWeather(weather)
                      .then()))
      );
  }

  private Station generateStation() {
    var stationName = UUID.randomUUID().toString();
    var active = random.nextBoolean();
    return Station.builder()
      .stationName(stationName)
      .active(active)
      .build();
  }

  private Weather generateWeather(Long stationId) {
    var temperature = random.nextInt(101) - 50;
    var windDurationValues = WindDurationType.values();
    var windDurationType = windDurationValues[random.nextInt(windDurationValues.length)];
    var windSpeed = random.nextInt(101);
    var precipitationTypesValues = PrecipitationType.values();
    var precipitationType = precipitationTypesValues[random.nextInt(precipitationTypesValues.length)];
    var cloudTypeValues = CloudType.values();
    var cloudType = cloudTypeValues[random.nextInt(cloudTypeValues.length)];
    var cloudMark = random.nextInt(100);
    var weatherDate = generateDate();
    return Weather.builder()
      .weatherDate(weatherDate)
      .createdAt(LocalDateTime.now())
      .cloudType(cloudType)
      .precipitation(precipitationType)
      .cloudMark(cloudMark)
      .temperature(temperature)
      .windSpeed(windSpeed)
      .windDuration(windDurationType)
      .stationId(stationId)
      .build();
  }

  private LocalDateTime generateDate() {
    var currentDateTime = LocalDateTime.now();
    var pastDateTime = currentDateTime.minusYears(5);

    var minDay = pastDateTime.toLocalDate().toEpochDay();
    var maxDay = currentDateTime.toLocalDate().toEpochDay();
    var randomDay = minDay + new Random().nextInt((int) (maxDay - minDay));

    return LocalDateTime.ofEpochSecond(randomDay * 24 * 60 * 60, 0, ZoneId.systemDefault().getRules().getOffset(currentDateTime));
  }
}
