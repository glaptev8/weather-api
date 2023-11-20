package org.weatherapi.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.redis.core.RedisHash;
import org.weatherapi.dto.WeatherDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("Station")
public class Station {
  private String stationName;
  private Boolean active;
  private List<Weather> weather;
  private LocalDateTime createdAt;

  public Station(String stationName, Boolean active, List<Weather> weather) {
    this.stationName = stationName;
    this.active = active;
    this.weather = weather;
  }
}
