package com.delivery.deliveryservice.nebagafeature;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafKaConsumer {

    private static final String TOPIC_NAME = "ordersTopic";

    private static final String GROUP_ID = "drakonishe";


    private String mess;

    @KafkaListener(topics = TOPIC_NAME, groupId = GROUP_ID)
    public void consume(String message) {
        mess = message;
    }

    public String getMessages() {
        return mess;
    }
}
