package com.delivery.deliveryservice.dto.response;

import jakarta.validation.constraints.NotNull;

public record CourierResponseDto(@NotNull Long courierId) {

}
