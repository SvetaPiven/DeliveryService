package com.delivery.deliveryservice.sevice;

import com.delivery.deliveryservice.dto.request.DeliveryCourierDTO;
import com.delivery.deliveryservice.dto.request.DeliverySetCourierDTO;
import com.delivery.deliveryservice.entity.Delivery;
import com.delivery.deliveryservice.entity.enumstatus.EnumStatus;
import com.delivery.deliveryservice.exception.CourierNotNeededException;
import com.delivery.deliveryservice.exception.DeletedDeliveryException;
import com.delivery.deliveryservice.exception.DeliveryNotFoundException;
import com.delivery.deliveryservice.exception.WrongEnumValueException;
import com.delivery.deliveryservice.repository.DeliveryRepository;
import com.delivery.deliveryservice.service.impl.DeliveryServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeliveryServiceTest {
    @Mock
    private DeliveryRepository deliveryRepository;
    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private DeliveryServiceImpl deliveryService;

    @Test
    void testCreateDelivery_CourierFound() {
        UUID deliveryUUID = UUID.randomUUID();
        Delivery delivery = new Delivery();
        delivery.setId(deliveryUUID);
        ReflectionTestUtils.setField(deliveryService, "urlUserService", "");
        when(deliveryRepository.save(any(Delivery.class))).thenReturn(delivery);

        ResponseEntity<Long> responseEntity = new ResponseEntity<>(123L, HttpStatus.FOUND);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Long.class))).thenReturn(responseEntity);

        deliveryService.createDelivery(String.valueOf(deliveryUUID));

        assertEquals(EnumStatus.COURIER_FOUND, delivery.getStatus());
        assertEquals(123L, delivery.getCourierId());
        Assertions.assertNotNull(delivery.getCourierId());
    }

    @Test
    void testCreateDelivery_CourierNotFound() {
        UUID deliveryUUID = UUID.randomUUID();
        Delivery delivery = new Delivery();
        delivery.setId(deliveryUUID);
        delivery.setStatus(EnumStatus.SEARCH_COURIER_FOR_DELIVERY);
        ReflectionTestUtils.setField(deliveryService, "urlUserService", "");
        when(deliveryRepository.save(any(Delivery.class))).thenReturn(delivery);

        when(restTemplate.exchange(anyString(), any(), any(), eq(Long.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        deliveryService.createDelivery(String.valueOf(deliveryUUID));

        verify(deliveryRepository, times(1)).save(any(Delivery.class));
        assertEquals(EnumStatus.SEARCH_COURIER_FOR_DELIVERY, delivery.getStatus());
        Assertions.assertNull(delivery.getCourierId());
    }

    @Test
    void testSetStatusSuccess() {
        DeliveryCourierDTO courierDTO = createValidCourierDTO();
        Delivery delivery = createValidDelivery();

        when(deliveryRepository.findById(any(UUID.class))).thenReturn(Optional.of(delivery));

        deliveryService.setStatus(courierDTO);

        verify(deliveryRepository, times(1)).findById(any(UUID.class));
        assertEquals(EnumStatus.RECEIVED, delivery.getStatus());
        Assertions.assertTrue(delivery.getIsDeleted());
    }

    @Test
    void testSetStatusWrongEnumValueException() {
        DeliveryCourierDTO courierDTO = createInvalidCourierDTO();

        assertThrows(WrongEnumValueException.class, () -> deliveryService.setStatus(courierDTO));
    }

    @Test
    void testSetStatusDeliveryNotFoundException() {
        DeliveryCourierDTO courierDTO = createValidCourierDTO();

        when(deliveryRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(DeliveryNotFoundException.class, () -> deliveryService.setStatus(courierDTO));
    }

    @Test
    void testSetStatusDeletedDeliveryException() {
        DeliveryCourierDTO courierDTO = createValidCourierDTO();
        Delivery deletedDelivery = createDeletedDelivery();

        when(deliveryRepository.findById(any(UUID.class))).thenReturn(Optional.of(deletedDelivery));

        assertThrows(DeletedDeliveryException.class, () -> deliveryService.setStatus(courierDTO));
    }

    private DeliveryCourierDTO createValidCourierDTO() {
        return new DeliveryCourierDTO(UUID.randomUUID().toString(), "Received");

    }

    private DeliveryCourierDTO createInvalidCourierDTO() {
        return new DeliveryCourierDTO(UUID.randomUUID().toString(), "Что-то...");
    }

    private Delivery createValidDelivery() {
        Delivery delivery = new Delivery();
        delivery.setId(UUID.randomUUID());
        delivery.setIsDeleted(false);
        return delivery;
    }

    private Delivery createDeletedDelivery() {
        Delivery delivery = new Delivery();
        delivery.setId(UUID.randomUUID());
        delivery.setIsDeleted(true);
        return delivery;
    }

    @Test
    void testGetStatusSuccess() {
        UUID deliveryId = UUID.randomUUID();
        EnumStatus expectedStatus = EnumStatus.RECEIVED;
        Delivery delivery = new Delivery();
        delivery.setId(deliveryId);
        delivery.setStatus(expectedStatus);

        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(delivery));

        EnumStatus actualStatus = deliveryService.getStatus(deliveryId.toString());

        assertEquals(expectedStatus, actualStatus);
    }

    @Test
    void testGetStatusDeliveryNotFoundException() {
        UUID uuid = UUID.randomUUID();

        when(deliveryRepository.findById(uuid)).thenReturn(Optional.empty());

        assertThrows(DeliveryNotFoundException.class, () -> deliveryService.getStatus(uuid.toString()));
    }

    @Test
    void testSetCourierSuccess() {
        UUID deliveryId = UUID.randomUUID();
        Long courierId = 123L;

        Delivery delivery = new Delivery();
        delivery.setId(deliveryId);
        delivery.setStatus(EnumStatus.SEARCH_COURIER_FOR_DELIVERY);

        DeliverySetCourierDTO deliverySetCourierDTO = new DeliverySetCourierDTO(String.valueOf(deliveryId), String.valueOf(courierId));

        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(delivery));

        deliveryService.setCourier(deliverySetCourierDTO);

        assertEquals(courierId, delivery.getCourierId());
        assertEquals(EnumStatus.COURIER_FOUND, delivery.getStatus());

    }

    @Test
    void testSetCourierDeliveryNotFoundException() {
        UUID randomUUID = UUID.randomUUID();

        when(deliveryRepository.findById(randomUUID)).thenReturn(Optional.empty());

        assertThrows(DeliveryNotFoundException.class, () -> deliveryService.setCourier(new DeliverySetCourierDTO(String.valueOf(randomUUID), String.valueOf(123L))));
    }

    @Test
    void testSetCourierCourierNotNeededException() {
        UUID randomUUID = UUID.randomUUID();

        Delivery delivery = new Delivery();
        delivery.setId(randomUUID);
        delivery.setStatus(EnumStatus.COURIER_FOUND);

        when(deliveryRepository.findById(randomUUID)).thenReturn(Optional.of(delivery));

        assertThrows(CourierNotNeededException.class, () -> deliveryService.setCourier(new DeliverySetCourierDTO(String.valueOf(randomUUID), String.valueOf(123L))));
    }
}
