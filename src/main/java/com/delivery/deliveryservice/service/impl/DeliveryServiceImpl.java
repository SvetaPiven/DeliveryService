package com.delivery.deliveryservice.service.impl;

import com.delivery.deliveryservice.dto.request.DeliveryCourierDTO;
import com.delivery.deliveryservice.dto.request.DeliverySetCourierDTO;
import com.delivery.deliveryservice.entity.Delivery;
import com.delivery.deliveryservice.entity.enumstatus.EnumStatus;
import com.delivery.deliveryservice.exception.CourierNotNeededException;
import com.delivery.deliveryservice.exception.DeletedDeliveryException;
import com.delivery.deliveryservice.exception.DeliveryNotFoundException;
import com.delivery.deliveryservice.exception.WrongEnumValueException;
import com.delivery.deliveryservice.repository.DeliveryRepository;
import com.delivery.deliveryservice.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.delivery.deliveryservice.entity.enumstatus.EnumStatus.COURIER_FOUND;
import static com.delivery.deliveryservice.entity.enumstatus.EnumStatus.RECEIVED;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {
    private final RestTemplate restTemplate;
    private final DeliveryRepository deliveryRepository;

    @Value("${url.userService}")
    private String urlUserService;

    @Transactional
    @Override
    public void createDelivery(String orderId) {
        Delivery delivery = deliveryRepository.save(Delivery.builder()
                .orderId(UUID.fromString(orderId))
                .created(LocalDateTime.now())
                .changed(LocalDateTime.now())
                .isDeleted(false)
                .status(EnumStatus.SEARCH_COURIER_FOR_DELIVERY)
                .build());

        ResponseEntity<Long> longResponseEntity;
        try {
            longResponseEntity = findCourier(delivery.getId());
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            return;
        }

        if (longResponseEntity.getStatusCode().is3xxRedirection()) {
            delivery.setCourierId(longResponseEntity.getBody());
            delivery.setStatus(COURIER_FOUND);
            delivery.setChanged(LocalDateTime.now());
        }
    }

    @Transactional
    @Override
    public void setStatus(DeliveryCourierDTO deliveryCourierDTO) {
        UUID deliveryId = UUID.fromString(deliveryCourierDTO.deliveryId());
        String deliveryStatus = deliveryCourierDTO.status();

        if (EnumStatus.fromValue(deliveryStatus) == null)
            throw new WrongEnumValueException("Status " +
                    deliveryStatus + " not found in UtilEnum");

        Delivery deliveryPersist = deliveryRepository.findById(deliveryId).orElseThrow(() ->
                new DeliveryNotFoundException("Id " + deliveryId + " not found"));

        if (Boolean.TRUE.equals(deliveryPersist.getIsDeleted()))
            throw new DeletedDeliveryException("Delivery is deleted");

        deliveryPersist.setStatus(EnumStatus.fromValue(deliveryStatus));
        deliveryPersist.setChanged(LocalDateTime.now());

        if (EnumStatus.fromValue(deliveryStatus) == RECEIVED)
            deliveryPersist.setIsDeleted(true);
    }

    @Transactional(readOnly = true)
    @Override
    public EnumStatus getStatus(String uuid) {
        Delivery deliveryPersist = deliveryRepository.findById(UUID.fromString(uuid)).orElseThrow(() ->
                new DeliveryNotFoundException("Id " + uuid + " not found"));
        return deliveryPersist.getStatus();
    }

    @Transactional
    @Override
    public void setCourier(DeliverySetCourierDTO deliverySetCourierDTO) {
        Long courierId = Long.valueOf(deliverySetCourierDTO.courierId());
        UUID deliveryId = UUID.fromString(deliverySetCourierDTO.deliveryId());
        Delivery deliveryPersist = deliveryRepository.findById(deliveryId).orElseThrow(() ->
                new DeliveryNotFoundException("Id " + deliveryId + " not found"));
        if (deliveryPersist.getStatus() == EnumStatus.SEARCH_COURIER_FOR_DELIVERY) {
            deliveryPersist.setCourierId(courierId);
            deliveryPersist.setStatus(COURIER_FOUND);
            deliveryPersist.setChanged(LocalDateTime.now());
        } else {
            throw new CourierNotNeededException("This delivery id " + deliveryId + " already have a courier");
        }
    }

    private ResponseEntity<Long> findCourier(UUID deliveryUUID) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(String.valueOf(deliveryUUID), headers);

        return restTemplate.exchange(
                urlUserService,
                HttpMethod.POST,
                request,
                Long.class
        );
    }
}
