package com.delivery.deliveryservice.controller;

import com.delivery.deliveryservice.nebagafeature.KafKaConsumer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/delivery")
@RestController
@RequiredArgsConstructor
public class DeliveryController {

    private final KafKaConsumer kafKaConsumer;

    @GetMapping
    public ResponseEntity<?> getMessage() {
        return ResponseEntity.ok(kafKaConsumer.getMessageFromTopic());
    }
}
