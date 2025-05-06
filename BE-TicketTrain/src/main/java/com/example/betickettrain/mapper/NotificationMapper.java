package com.example.betickettrain.mapper;

import com.example.betickettrain.dto.NotificationDto;
import com.example.betickettrain.entity.Notification;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface NotificationMapper {
    Notification toEntity(NotificationDto notificationDto);

    NotificationDto toDto(Notification notification);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Notification partialUpdate(NotificationDto notificationDto, @MappingTarget Notification notification);
}