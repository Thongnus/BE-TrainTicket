package com.example.betickettrain.mapper;

import com.example.betickettrain.dto.PaymentDto;
import com.example.betickettrain.entity.Payment;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface PaymentMapper {
    Payment toEntity(PaymentDto paymentDto);

    PaymentDto toDto(Payment payment);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Payment partialUpdate(PaymentDto paymentDto, @MappingTarget Payment payment);
}