package com.example.betickettrain.mapper;

import com.example.betickettrain.dto.CancellationDto;
import com.example.betickettrain.entity.Cancellation;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CancellationMapper {
    Cancellation toEntity(CancellationDto cancellationDto);

    CancellationDto toDto(Cancellation cancellation);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Cancellation partialUpdate(CancellationDto cancellationDto, @MappingTarget Cancellation cancellation);
}