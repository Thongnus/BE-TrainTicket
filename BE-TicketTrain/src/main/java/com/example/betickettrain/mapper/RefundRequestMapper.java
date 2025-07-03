package com.example.betickettrain.mapper;

import com.example.betickettrain.dto.RefundRequestDto;
import com.example.betickettrain.entity.RefundRequest;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface RefundRequestMapper {
    RefundRequest toEntity(RefundRequestDto refundRequestDto);


    @Mapping(target = "bookingId", source = "booking.bookingId")
    @Mapping(target = "bookingCode", source = "booking.bookingCode")
    @Mapping(target = "customerName", source = "booking.user.fullName")
    @Mapping(target = "customerEmail", source = "booking.contactEmail")
    @Mapping(target = "customerPhone", source = "booking.contactPhone")
    @Mapping(target = "bookingStatus", source = "booking.bookingStatus")
    @Mapping(target = "paymentId", source = "payment.paymentId")
    @Mapping(target = "paymentAmount", source = "payment.paymentAmount")
    @Mapping(target = "paymentStatus", source = "payment.status")
    @Mapping(target = "paymentMethod", source = "payment.paymentMethod")
    @Mapping(target = "refundPolicyId", source = "refundPolicy.policyId")
    @Mapping(target = "policyName", source = "refundPolicy.policyName")
    @Mapping(target = "policyRefundPercent", source = "refundPolicy.refundPercent")

    RefundRequestDto toDto(RefundRequest refundRequest);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    RefundRequest partialUpdate(RefundRequestDto refundRequestDto, @MappingTarget RefundRequest refundRequest);
}