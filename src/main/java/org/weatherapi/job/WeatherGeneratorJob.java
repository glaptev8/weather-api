package org.weatherapi.job;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.weatherapi.dto.CloudType;
import org.weatherapi.dto.PrecipitationType;
import org.weatherapi.dto.StationDto;
import org.weatherapi.dto.WeatherDto;
import org.weatherapi.dto.WindDurationType;
import org.weatherapi.entity.Station;
import org.weatherapi.entity.Weather;
import org.weatherapi.service.api.StationService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@EnableScheduling
@Profile("!test")
@RequiredArgsConstructor
public class WeatherGeneratorJob {

  private final StationService stationService;
  private final static Random random = new Random();

  @Scheduled(fixedRate = 3 * 60 * 60 * 1000)
  public void generateStation() {
    stationService
      .addStation(generateRandomStation())
      .repeat(random.nextInt(3))
      .thenMany(stationService.getAllStation()
                  .collectList()
                  .flatMap(stations -> {
                    var weather = generateRandomWeather(stations
                                                          .stream()
                                                          .map(StationDto::getStationName)
                                                          .toList());
                    if (weather != null) {
                      return stationService
                        .addWeatherToStation(weather)
                        .then();
                    } else {
                      return Mono.empty().then();
                    }
                  })
                  .repeat(random.nextInt(6))
      )
      .subscribe();
  }

  public static Station generateRandomStation() {
    var stationName = UUID.randomUUID().toString();
    var active = random.nextBoolean();
    return new Station(stationName, active, new ArrayList<>());
  }

  public static Weather generateRandomWeather(List<String> stations) {
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
    var stationId = random.nextInt(stations.size());
    String stationName = stations.get(stationId);
    if (stationName != null) {
      return new Weather(temperature, windDurationType, windSpeed, precipitationType, cloudType, cloudMark, stationName, weatherDate, LocalDateTime.now());
    }
    return null;
  }

  private static LocalDateTime generateDate() {
    var currentDateTime = LocalDateTime.now();
    var pastDateTime = currentDateTime.minusYears(5);

    var minDay = pastDateTime.toLocalDate().toEpochDay();
    var maxDay = currentDateTime.toLocalDate().toEpochDay();
    var randomDay = minDay + new Random().nextInt((int) (maxDay - minDay));

    return LocalDateTime.ofEpochSecond(randomDay * 24 * 60 * 60, 0, ZoneId.systemDefault().getRules().getOffset(currentDateTime));
  }
}
