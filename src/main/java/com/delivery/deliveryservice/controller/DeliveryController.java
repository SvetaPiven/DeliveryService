package com.delivery.deliveryservice.controller;

import com.delivery.deliveryservice.service.DeliveryService;
import com.fasterxml.jackson.databind.introspect.TypeResolutionContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequestMapping("/api/delivery")
@RestController
@RequiredArgsConstructor
public class DeliveryController {

//    private final KafKaConsumer kafKaConsumer;

    //    @GetMapping
//    public ResponseEntity<?> getMessage() {
//        return ResponseEntity.ok(kafKaConsumer.getMessageFromTopic());
//    }
    private final DeliveryService deliveryService;


    @PatchMapping("/{id}/status")
    public ResponseEntity<?> setStatus(@PathVariable("id") UUID uuid, @RequestBody String status) {
        deliveryService.setStatus(uuid, status);
        return ResponseEntity.
                status(HttpStatus.ACCEPTED)
                .build();
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<String> getStatus(@PathVariable("id") UUID uuid) {
        return ResponseEntity.
                status(HttpStatus.ACCEPTED)
                .body(deliveryService.getStatus(uuid));
    }
}
