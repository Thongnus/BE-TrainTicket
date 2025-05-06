package com.example.betickettrain.mapper;

import com.example.betickettrain.dto.BookingPromotionDto;
import com.example.betickettrain.entity.BookingPromotion;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface BookingPromotionMapper {
    BookingPromotion toEntity(BookingPromotionDto bookingPromotionDto);

    BookingPromotionDto toDto(BookingPromotion bookingPromotion);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    BookingPromotion partialUpdate(BookingPromotionDto bookingPromotionDto, @MappingTarget BookingPromotion bookingPromotion);
}