package com.example.betickettrain.mapper;

import com.example.betickettrain.dto.SystemLogDto;
import com.example.betickettrain.entity.SystemLog;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface SystemLogMapper {
    SystemLog toEntity(SystemLogDto systemLogDto);

    SystemLogDto toDto(SystemLog systemLog);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    SystemLog partialUpdate(SystemLogDto systemLogDto, @MappingTarget SystemLog systemLog);
}