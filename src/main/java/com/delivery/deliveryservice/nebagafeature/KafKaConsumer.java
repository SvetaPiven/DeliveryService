package com.delivery.deliveryservice.nebagafeature;

import com.delivery.deliveryservice.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.LinkedList;

@Service
@RequiredArgsConstructor
public class KafKaConsumer {
    private final DeliveryService deliveryService;

    private static final String TOPIC_NAME = "ordersTopic";

    private static final String GROUP_ID = "drakonishe";

//    private LinkedList<String> messages = new LinkedList<>();

    @KafkaListener(topics = TOPIC_NAME, groupId = GROUP_ID)
    public void consume(String message) {
//        messages.addLast(message);
        deliveryService.createDelivery(message);
    }

//    public String getMessageFromTopic() {
//        return messages.pollFirst();
//    }
}
