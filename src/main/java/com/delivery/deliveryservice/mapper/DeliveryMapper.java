package com.delivery.deliveryservice.mapper;

import com.delivery.deliveryservice.dto.request.OrderRequestDto;
import com.delivery.deliveryservice.dto.response.CourierResponseDto;
import com.delivery.deliveryservice.entity.Delivery;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = "spring")
public interface DeliveryMapper {

    Delivery toDeliveryFromOrderRequestDto(OrderRequestDto orderRequestDto);

    Delivery toDeliveryFromCourierResponseDto(CourierResponseDto courierResponseDto);

}
