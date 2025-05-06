package com.example.betickettrain.mapper;

import com.example.betickettrain.dto.PromotionDto;
import com.example.betickettrain.entity.Promotion;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface PromotionMapper {
    Promotion toEntity(PromotionDto promotionDto);

    PromotionDto toDto(Promotion promotion);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Promotion partialUpdate(PromotionDto promotionDto, @MappingTarget Promotion promotion);
}