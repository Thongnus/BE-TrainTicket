package com.example.betickettrain.mapper;

import com.example.betickettrain.dto.RouteStationDto;
import com.example.betickettrain.entity.RouteStation;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface RouteStationMapper {
   // @InheritConfiguration // cùng chiều
    @InheritInverseConfiguration  // ngươc chiều
    RouteStation toEntity(RouteStationDto routeStationDto);

    @Mapping(source = "route.routeId", target = "routeId")
    @Mapping(source = "station.stationId", target = "stationId")
    @Mapping(source = "station.stationName", target = "stationName")
    RouteStationDto toDto(RouteStation routeStation);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    RouteStation partialUpdate(RouteStationDto routeStationDto, @MappingTarget RouteStation routeStation);
}