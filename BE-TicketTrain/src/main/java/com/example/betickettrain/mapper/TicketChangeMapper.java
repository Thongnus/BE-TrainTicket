package com.example.betickettrain.mapper;

import com.example.betickettrain.dto.TicketChangeDto;
import com.example.betickettrain.entity.TicketChange;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface TicketChangeMapper {
    TicketChange toEntity(TicketChangeDto ticketChangeDto);

    TicketChangeDto toDto(TicketChange ticketChange);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    TicketChange partialUpdate(TicketChangeDto ticketChangeDto, @MappingTarget TicketChange ticketChange);
}