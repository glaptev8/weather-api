package org.weatherapi.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.weatherapi.entity.Station;

public interface StationPostgresRepository extends R2dbcRepository<Station, Long>  {
}
