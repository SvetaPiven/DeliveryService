package com.delivery.deliveryservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.UUID;

@Schema(description = "Установка статуса доставки")
public record DeliveryCourierDTO(
        @Schema(description = "ID доставки", example = "3fa85f64-5717-4562-b3fc-2c963f66afa0")
        @UUID @NotNull(message = "deliveryId is required") String deliveryId,
        @Schema(description = "Статус доставки", example = "Search courier for delivery")
        @NotNull(message = "deliveryStatus is required") String status) {
}
