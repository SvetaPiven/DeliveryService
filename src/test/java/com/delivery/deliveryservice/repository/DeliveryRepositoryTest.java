package com.delivery.deliveryservice.repository;

import com.delivery.deliveryservice.entity.Delivery;
import com.delivery.deliveryservice.entity.enumstatus.EnumStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Testcontainers
@TestPropertySource(locations = "classpath:application.yml")
class DeliveryRepositoryTest {
    @Autowired
    DeliveryRepository deliveryRepository;

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:alpine");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Test
    void saveAndFindById_withValidDelivery_returnDeliveryAndSaveDelivery() {
        Delivery deliveryTransient = Delivery.builder()
                .orderId(UUID.randomUUID())
                .courierId(1L)
                .status(EnumStatus.COURIER_FOUND)
                .created(LocalDateTime.now())
                .changed(LocalDateTime.now())
                .isDeleted(false)
                .build();

        Delivery deliveryPersist = deliveryRepository.save(deliveryTransient);
        assertNotNull(deliveryPersist.getId());
        Delivery findDelivery = deliveryRepository.findById(deliveryPersist.getId()).orElse(null);

        assertEquals(deliveryPersist, findDelivery);
        assertNotNull(findDelivery);

    }
}
