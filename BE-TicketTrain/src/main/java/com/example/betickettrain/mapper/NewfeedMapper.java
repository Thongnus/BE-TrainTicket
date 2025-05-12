package com.example.betickettrain.mapper;

import com.example.betickettrain.dto.NewfeedDto;
import com.example.betickettrain.entity.Newfeed;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface NewfeedMapper {
    Newfeed toEntity(NewfeedDto newfeedDto);

    NewfeedDto toDto(Newfeed newfeed);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Newfeed partialUpdate(NewfeedDto newfeedDto, @MappingTarget Newfeed newfeed);
}