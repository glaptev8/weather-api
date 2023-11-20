package org.weatherapi.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.weatherapi.dto.StationDto;
import org.weatherapi.dto.UserDto;
import org.weatherapi.entity.Station;
import org.weatherapi.entity.User;

@Mapper(componentModel = "spring")
public interface MapStructMapper {
  @Mapping(source = "name", target = "name")
  @Mapping(source = "apiKey", target = "apiKey")
  UserDto userToDto(User user);
  User userToEntity(UserDto userDto);

  StationDto stationToDto(Station station);
  Station stationToEntity(StationDto station);
}
