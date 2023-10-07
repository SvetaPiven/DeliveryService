package com.delivery.deliveryservice.kafka;

import com.delivery.deliveryservice.service.DeliveryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
class KafkaConsumerTest {
    @Autowired
    private KafkaProducer producer;
    @Value("${test.topic}")
    private String topic;
    @MockBean
    private DeliveryService deliveryService;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.liquibase.enabled", () -> false);
    }

    @Test
    void givenEmbeddedKafkaBroker_whenSendingMessage_thenDeliveryServiceCalled() throws InterruptedException {
        String data = "Sending with our own simple KafkaProducer";

        producer.send(topic, data);

        TimeUnit.SECONDS.sleep(1);

        verify(deliveryService, times(1)).createDelivery(data);
    }

    @Test
    void givenEmbeddedKafkaBroker_whenSendingMultipleMessages_thenDeliveryServiceCalledMultipleTimes() throws InterruptedException {
        String data1 = "Message 1";
        String data2 = "Message 2";

        producer.send(topic, data1);
        producer.send(topic, data2);

        TimeUnit.SECONDS.sleep(1);

        verify(deliveryService, times(1)).createDelivery(data1);
        verify(deliveryService, times(1)).createDelivery(data2);
        verify(deliveryService, times(2)).createDelivery(any());
    }

    @Test
    void fail() throws InterruptedException {
        String data1 = "Message 1";
        String data2 = "Message 2";
        String data3 = "Message 3";

        producer.send(topic, data1);
        producer.send(topic, data2);

        TimeUnit.SECONDS.sleep(1);

        verify(deliveryService, times(1)).createDelivery(data1);
        verify(deliveryService, times(1)).createDelivery(data2);
        verify(deliveryService, times(0)).createDelivery(data3);
        verify(deliveryService, times(2)).createDelivery(any());
    }
}
