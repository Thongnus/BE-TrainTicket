package com.example.betickettrain.mapper;

import com.example.betickettrain.dto.TrainDto;
import com.example.betickettrain.entity.Train;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface TrainMapper {
    Train toEntity(TrainDto trainDTO);

    TrainDto toDto(Train train);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Train partialUpdate(TrainDto trainDTO, @MappingTarget Train train);
}