package com.example.betickettrain.mapper;

import com.example.betickettrain.dto.RouteDto;
import com.example.betickettrain.entity.Route;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface RouteMapper {
    Route toEntity(RouteDto routeDto);

    RouteDto toDto(Route route);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Route partialUpdate(RouteDto routeDto, @MappingTarget Route route);
}