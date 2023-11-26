package org.weatherapi.dto;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
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
  @JsonFormat(pattern = "yyyy-MM-dd HH")
  @DateTimeFormat(pattern = "yyyy-MM-dd HH")
  private LocalDateTime weatherDate;
  @JsonFormat(pattern = "yyyy-MM-dd HH")
  @DateTimeFormat(pattern = "yyyy-MM-dd HH")
  private LocalDateTime createdAt;
}
