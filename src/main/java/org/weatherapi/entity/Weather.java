package org.weatherapi.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.format.annotation.DateTimeFormat;
import org.weatherapi.dto.CloudType;
import org.weatherapi.dto.PrecipitationType;
import org.weatherapi.dto.WindDurationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Weather {
  @Id
  private Long id;
  private Integer temperature;
  private WindDurationType windDuration;
  private Integer windSpeed;
  private PrecipitationType precipitation;
  private CloudType cloudType;
  private Integer cloudMark;
  private Long stationId;
  @DateTimeFormat(pattern = "yyyy-MM-dd HH")
  private LocalDateTime weatherDate;
  @DateTimeFormat(pattern = "yyyy-MM-dd HH")
  private LocalDateTime createdAt;
}
