package com.delivery.deliveryservice.nebagafeature;

import com.delivery.deliveryservice.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaConsumer {
    private final DeliveryService deliveryService;

    private static final String TOPIC_NAME = "ordersTopic";
    private static final String GROUP_ID = "drakonishe";

    @KafkaListener(topics = TOPIC_NAME, groupId = GROUP_ID)
    public void consume(String message) {
        deliveryService.createDelivery(message);
    }
}
