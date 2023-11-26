package org.weatherapi.dto;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StationDto {
  private String stationName;
  private Boolean active;
  private List<WeatherDto> weather;
  @JsonFormat(pattern = "yyyy-MM-dd HH")
  @DateTimeFormat(pattern = "yyyy-MM-dd HH")
  private LocalDateTime createdAt;
}
