package com.delivery.deliveryservice.controller;

import com.delivery.deliveryservice.dto.response.ErrorResponseDTO;
import com.delivery.deliveryservice.exception.CourierNotNeededException;
import com.delivery.deliveryservice.exception.DeletedDeliveryException;
import com.delivery.deliveryservice.exception.DeliveryNotFoundException;
import com.delivery.deliveryservice.exception.WrongEnumValueException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class AdviceController {
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({DeliveryNotFoundException.class})
    public ErrorResponseDTO handleException(DeliveryNotFoundException e) {
        return new ErrorResponseDTO(
                e.getMessage(),
                LocalDateTime.now()
        );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({WrongEnumValueException.class, DeletedDeliveryException.class})
    public ErrorResponseDTO handleException(RuntimeException e) {
        return new ErrorResponseDTO(
                e.getMessage(),
                LocalDateTime.now()
        );
    }

    @ResponseStatus(HttpStatus.I_AM_A_TEAPOT)
    @ExceptionHandler({CourierNotNeededException.class})
    public ErrorResponseDTO handleException(CourierNotNeededException e) {
        return new ErrorResponseDTO(
                e.getMessage(),
                LocalDateTime.now()
        );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({NumberFormatException.class})
    public ErrorResponseDTO handleException() {
        return new ErrorResponseDTO(
                "Courier id not valid",
                LocalDateTime.now()
        );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponseDTO handleValidationException(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> new StringBuilder(e.getField()).append(": ").append(e.getDefaultMessage()))
                .collect(Collectors.joining(", "));

        return new ErrorResponseDTO(
                errors, LocalDateTime.now()
        );
    }


}
