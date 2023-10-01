package com.delivery.deliveryservice.dto.request;

import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.UUID;

public record DeliveryIdDTO(@UUID @NotNull(message = "deliveryId is required") String deliveryId) {
}
