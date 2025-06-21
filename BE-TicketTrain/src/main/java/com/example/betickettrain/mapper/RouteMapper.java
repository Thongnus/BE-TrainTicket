package com.example.betickettrain.mapper;

import com.example.betickettrain.dto.RouteDto;
import com.example.betickettrain.entity.Route;
import com.example.betickettrain.entity.Station;
import org.mapstruct.*;
 //chưa rõ cần xem lại
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RouteMapper {

    @Named("stationFromId")
    static Station mapStationId(Integer id) {
        if (id == null) return null;
        Station station = new Station();
        station.setStationId(id);
        return station;
    }

     @Mapping(target = "originStation", source = "originStationId", qualifiedByName = "stationFromId")
     @Mapping(target = "destinationStation", source = "destinationStationId", qualifiedByName = "stationFromId")
     Route toEntity(RouteDto routeDto);


    @Mapping(source = "originStation.stationId", target = "originStationId")
    @Mapping(source = "destinationStation.stationId", target = "destinationStationId")
    @Mapping(source = "originStation.stationName", target = "originStationName")
    @Mapping(source = "destinationStation.stationName", target = "destinationStationName")
    RouteDto toDto(Route entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Route partialUpdate(RouteDto routeDto, @MappingTarget Route route);
}
