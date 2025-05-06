package com.example.betickettrain.mapper;

import com.example.betickettrain.dto.TripScheduleDto;
import com.example.betickettrain.entity.TripSchedule;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface TripScheduleMapper {
    TripSchedule toEntity(TripScheduleDto tripScheduleDto);

    TripScheduleDto toDto(TripSchedule tripSchedule);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    TripSchedule partialUpdate(TripScheduleDto tripScheduleDto, @MappingTarget TripSchedule tripSchedule);
}