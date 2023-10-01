package com.delivery.deliveryservice.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record ErrorResponseDTO(String message,
                               @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime localDateTime) {
}
