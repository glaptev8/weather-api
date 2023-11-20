package org.weatherapi.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherDto {
  private Integer temperature;
  private WindDurationType windDuration;
  private Integer windSpeed;
  private PrecipitationType precipitation;
  private CloudType cloudType;
  private Integer cloudMark;
  private String stationName;
  private LocalDateTime weatherDate;
  private LocalDateTime createdAt;
}
