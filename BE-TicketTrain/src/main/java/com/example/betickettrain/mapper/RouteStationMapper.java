package com.example.betickettrain.mapper;

import com.example.betickettrain.dto.RouteStationDto;
import com.example.betickettrain.entity.RouteStation;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface RouteStationMapper {
    RouteStation toEntity(RouteStationDto routeStationDto);

    RouteStationDto toDto(RouteStation routeStation);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    RouteStation partialUpdate(RouteStationDto routeStationDto, @MappingTarget RouteStation routeStation);
}