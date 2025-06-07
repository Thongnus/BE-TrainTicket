package com.example.betickettrain.mapper;

import com.example.betickettrain.dto.BookingDto;
import com.example.betickettrain.entity.Booking;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface BookingMapper {
    Booking toEntity(BookingDto bookingDto);

    BookingDto toDto(Booking booking);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Booking partialUpdate(BookingDto bookingDto, @MappingTarget Booking booking);


}