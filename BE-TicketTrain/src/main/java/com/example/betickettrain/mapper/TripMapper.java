package com.example.betickettrain.mapper;

import com.example.betickettrain.dto.TripDto;
import com.example.betickettrain.entity.Trip;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = {RouteMapper.class, TrainMapper.class} )// 👈 luôn thêm mapper con ở đây)

public interface TripMapper {
    Trip toEntity(TripDto tripDto);

    TripDto toDto(Trip trip);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Trip partialUpdate(TripDto tripDto, @MappingTarget Trip trip);
}