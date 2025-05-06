package com.example.betickettrain.mapper;

import com.example.betickettrain.dto.SeatDto;
import com.example.betickettrain.entity.Seat;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface SeatMapper {
    Seat toEntity(SeatDto seatDto);

    SeatDto toDto(Seat seat);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Seat partialUpdate(SeatDto seatDto, @MappingTarget Seat seat);
}