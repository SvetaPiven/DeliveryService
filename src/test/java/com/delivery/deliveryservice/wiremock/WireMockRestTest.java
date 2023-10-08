package com.delivery.deliveryservice.wiremock;

import com.delivery.deliveryservice.entity.Delivery;
import com.delivery.deliveryservice.repository.DeliveryRepository;
import com.delivery.deliveryservice.service.DeliveryService;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;

import java.util.UUID;

import static com.delivery.deliveryservice.entity.enumstatus.EnumStatus.COURIER_FOUND;
import static com.delivery.deliveryservice.entity.enumstatus.EnumStatus.SEARCH_COURIER_FOR_DELIVERY;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestPropertySource(locations = "classpath:application.yml")
@WireMockTest(httpPort = 8085)
class WireMockRestTest {
    @MockBean
    private DeliveryRepository deliveryRepository;
    @Autowired
    private DeliveryService deliveryService;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.liquibase.enabled", () -> false);
    }

    @Test
    void testCreateDeliveryWithCourierFound() {
        stubFor(post(urlEqualTo("/api/v1/courier"))
                .willReturn(aResponse()
                        .withStatus(302)
                        .withHeader("Content-Type", "application/json")
                        .withBody("1")));

        UUID uuid = UUID.randomUUID();
        Delivery delivery = new Delivery();
        delivery.setId(uuid);
        delivery.setStatus(SEARCH_COURIER_FOR_DELIVERY);
        when(deliveryRepository.save(any(Delivery.class))).thenReturn(delivery);

        deliveryService.createDelivery(String.valueOf(uuid));

        verify(deliveryRepository, times(1)).save(any());
        assertEquals(1L, delivery.getCourierId());
        assertEquals(COURIER_FOUND, delivery.getStatus());

        WireMock.verify(postRequestedFor(urlEqualTo("/api/v1/courier")));
    }

    @Test
    void testCreateDeliveryWithCourierNotFound() {
        stubFor(post(urlEqualTo("/api/v1/courier"))
                .willReturn(aResponse()
                        .withStatus(404)));

        UUID uuid = UUID.randomUUID();
        Delivery delivery = new Delivery();
        delivery.setId(uuid);
        delivery.setStatus(SEARCH_COURIER_FOR_DELIVERY);
        when(deliveryRepository.save(any(Delivery.class))).thenReturn(delivery);

        deliveryService.createDelivery(String.valueOf(uuid));

        verify(deliveryRepository, times(1)).save(any());
        assertNull(delivery.getCourierId());
        assertEquals(SEARCH_COURIER_FOR_DELIVERY, delivery.getStatus());
        WireMock.verify(postRequestedFor(urlEqualTo("/api/v1/courier")));
    }
}
