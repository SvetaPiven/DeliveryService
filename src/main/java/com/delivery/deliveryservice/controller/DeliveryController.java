package com.delivery.deliveryservice.controller;

import com.delivery.deliveryservice.dto.request.DeliveryCourierDTO;
import com.delivery.deliveryservice.dto.request.DeliveryIdDTO;
import com.delivery.deliveryservice.dto.request.DeliverySetCourierDTO;
import com.delivery.deliveryservice.entity.enumstatus.EnumStatus;
import com.delivery.deliveryservice.service.DeliveryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/delivery")
@RestController
@RequiredArgsConstructor
public class DeliveryController {
    private final DeliveryService deliveryService;

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PatchMapping("/status")
    public void setStatus(@RequestBody @Valid DeliveryCourierDTO deliveryCourierDTO) {
        deliveryService.setStatus(deliveryCourierDTO);
    }

    @GetMapping("/status")
    public ResponseEntity<EnumStatus> getStatus(@RequestBody @Valid DeliveryIdDTO deliveryIdDTO) {
        return ResponseEntity.
                status(HttpStatus.ACCEPTED)
                .body(deliveryService.getStatus(deliveryIdDTO.deliveryId()));
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PatchMapping("/courier")
    public void setCourier(@RequestBody @Valid DeliverySetCourierDTO deliverySetCourierDTO) {
        deliveryService.setCourier(deliverySetCourierDTO);
    }
}
