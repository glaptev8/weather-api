package org.weatherapi.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.weatherapi.entity.Weather;

public interface WeatherPostgresRepository extends R2dbcRepository<Weather, Long> {
}
