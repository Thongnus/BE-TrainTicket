package com.example.betickettrain.mapper;

import com.example.betickettrain.dto.TicketDto;
import com.example.betickettrain.entity.Ticket;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface TicketMapper {
    Ticket toEntity(TicketDto ticketDto);

    TicketDto toDto(Ticket ticket);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Ticket partialUpdate(TicketDto ticketDto, @MappingTarget Ticket ticket);
}