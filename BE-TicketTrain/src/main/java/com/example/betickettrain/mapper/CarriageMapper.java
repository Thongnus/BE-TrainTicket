package com.example.betickettrain.mapper;

import com.example.betickettrain.dto.CarriageDto;
import com.example.betickettrain.entity.Carriage;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CarriageMapper {
    Carriage toEntity(CarriageDto carriageDto);

    CarriageDto toDto(Carriage carriage);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Carriage partialUpdate(CarriageDto carriageDto, @MappingTarget Carriage carriage);
}