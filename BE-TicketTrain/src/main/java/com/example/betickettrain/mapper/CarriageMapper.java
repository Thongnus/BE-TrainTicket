package com.example.betickettrain.mapper;

import com.example.betickettrain.dto.CarriageDto;
import com.example.betickettrain.entity.Carriage;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING,
uses = {SeatMapper.class}) // Use SeatMapper to map seats within Carriage
public interface CarriageMapper {
    @Mapping(source = "train.trainId", target = "trainId")
    @Mapping(source = "train.trainName", target = "trainName")
    CarriageDto toDto(Carriage carriage);
  //  @Mapping(target = "train", ignore = true) // bỏ qua, sẽ set bằng tay ở Service
    Carriage toEntity(CarriageDto carriageDto);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Carriage partialUpdate(CarriageDto carriageDto, @MappingTarget Carriage carriage);
}