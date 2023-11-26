package org.weatherapi.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Station {
  @Id
  private Long id;
  private String stationName;
  private Boolean active;
  @DateTimeFormat(pattern = "yyyy-MM-dd HH")
  private LocalDateTime createdAt;
}
