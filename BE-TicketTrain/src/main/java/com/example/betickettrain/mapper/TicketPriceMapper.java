package com.example.betickettrain.mapper;

import com.example.betickettrain.dto.TicketPriceDto;
import com.example.betickettrain.entity.TicketPrice;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface TicketPriceMapper {
    TicketPrice toEntity(TicketPriceDto ticketPriceDto);

    TicketPriceDto toDto(TicketPrice ticketPrice);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    TicketPrice partialUpdate(TicketPriceDto ticketPriceDto, @MappingTarget TicketPrice ticketPrice);
}