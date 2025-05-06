package com.example.betickettrain.mapper;

import com.example.betickettrain.dto.TrainDTO;
import com.example.betickettrain.entity.Train;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface TrainMapper {
    Train toEntity(TrainDTO trainDTO);

    TrainDTO toDto(Train train);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Train partialUpdate(TrainDTO trainDTO, @MappingTarget Train train);
}