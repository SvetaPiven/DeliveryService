package com.delivery.deliveryservice.nebagafeature;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.LinkedList;

@Service
public class KafKaConsumer {

    private static final String TOPIC_NAME = "ordersTopic";

    private static final String GROUP_ID = "drakonishe";

    private LinkedList<String> messages = new LinkedList<>();

    @KafkaListener(topics = TOPIC_NAME, groupId = GROUP_ID)
    public void consume(String message) {
        messages.addLast(message);
    }

    public String getMessageFromTopic() {
        return messages.pollFirst();
    }
}
