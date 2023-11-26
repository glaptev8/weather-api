package org.weatherapi.job;

import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.weatherapi.service.api.WeatherGeneratorService;

import lombok.RequiredArgsConstructor;

@Component
@EnableScheduling
@Profile("!test")
@RequiredArgsConstructor
public class WeatherGeneratorJob {

  private final WeatherGeneratorService weatherGeneratorService;

  @Scheduled(fixedRate = 3 * 60 * 60 * 1000)
  public void generateStation() {
    weatherGeneratorService.generateStationAndWeather()
      .subscribe();
  }
}
