package com.delivery.deliveryservice.dto.request;

import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.UUID;

public record DeliveryCourierDTO(@UUID @NotNull(message = "deliveryId is required") String deliveryId,
                                 @NotNull(message = "deliveryStatus is required") String status) {
}
