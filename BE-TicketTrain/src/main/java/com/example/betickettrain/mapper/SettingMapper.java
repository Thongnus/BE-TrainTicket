package com.example.betickettrain.mapper;

import com.example.betickettrain.dto.SettingDto;
import com.example.betickettrain.entity.Setting;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface SettingMapper {
    Setting toEntity(SettingDto settingDto);

    SettingDto toDto(Setting setting);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Setting partialUpdate(SettingDto settingDto, @MappingTarget Setting setting);
}