package org.weatherapi.entity;

import java.time.LocalDateTime;

import org.weatherapi.dto.CloudType;
import org.weatherapi.dto.PrecipitationType;
import org.weatherapi.dto.WindDurationType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Weather {
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
