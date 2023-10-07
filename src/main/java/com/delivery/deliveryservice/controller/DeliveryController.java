package com.delivery.deliveryservice.controller;

import com.delivery.deliveryservice.dto.request.DeliveryCourierDTO;
import com.delivery.deliveryservice.dto.request.DeliveryIdDTO;
import com.delivery.deliveryservice.dto.request.DeliverySetCourierDTO;
import com.delivery.deliveryservice.dto.response.ErrorResponseDTO;
import com.delivery.deliveryservice.entity.enumstatus.EnumStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Контроллер для создания, просмотра и изменения информации о доставке")
public interface DeliveryController {

    @Operation(summary = "Установка статуса доставки",
            description = "Данный эндпоинт предназначен для установки статуса доставки в таблице deliveries")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "202",
                    description = "Запрос принят",
                    content = {@Content(schema = @Schema(implementation = String.class))}
            ),

            @ApiResponse(
                    responseCode = "400",
                    description = "Ошибка валидации",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseDTO.class))}
            ),

            @ApiResponse(
                    responseCode = "404",
                    description = "Доставка не найдена в базе данных",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseDTO.class))}
            ),

            @ApiResponse(
                    responseCode = "409",
                    description = "Доставка была выполнена и удалена",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseDTO.class))}
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseDTO.class))}
            )
    })
    public void setStatus(@RequestBody @Valid DeliveryCourierDTO deliveryCourierDTO);

    @Operation(summary = "Поиск статуса доставки",
            description = "Данный эндпоинт предназначен для получения статуса доставки из таблицы deliveries")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "202",
                    description = "Запрос принят",
                    content = {@Content(schema = @Schema(implementation = EnumStatus.class))}
            ),

            @ApiResponse(
                    responseCode = "400",
                    description = "Ошибка валидации",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseDTO.class))}
            ),

            @ApiResponse(
                    responseCode = "404",
                    description = "Доставка не найдена в базе данных",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseDTO.class))}
            ),

            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseDTO.class))}
            )
    })
    public ResponseEntity<EnumStatus> getStatus(@RequestBody @Valid DeliveryIdDTO deliveryIdDTO);

    @Operation(summary = "Поиск статуса доставки",
            description = "Данный эндпоинт предназначен для получения статуса доставки из таблицы deliveries")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "202",
                    description = "Запрос принят",
                    content = {@Content(schema = @Schema(implementation = String.class))}
            ),

            @ApiResponse(
                    responseCode = "400",
                    description = "Ошибка валидации",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseDTO.class))}
            ),

            @ApiResponse(
                    responseCode = "404",
                    description = "Доставка не найдена в базе данных",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseDTO.class))}
            ),

            @ApiResponse(
                    responseCode = "418",
                    description = "У данной доставки уже есть курьер",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseDTO.class))}
            ),

            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = {@Content(schema = @Schema(implementation = ErrorResponseDTO.class))}
            )
    })
    public void setCourier(@RequestBody @Valid DeliverySetCourierDTO deliverySetCourierDTO);
}
