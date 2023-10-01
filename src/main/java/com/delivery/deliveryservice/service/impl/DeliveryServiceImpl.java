package com.delivery.deliveryservice.service.impl;

import com.delivery.deliveryservice.dto.request.DeliveryCourierDTO;
import com.delivery.deliveryservice.entity.Delivery;
import com.delivery.deliveryservice.entity.enumstatus.EnumStatus;
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
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.delivery.deliveryservice.entity.enumstatus.EnumStatus.COURIER_FOUND;
import static com.delivery.deliveryservice.entity.enumstatus.EnumStatus.RECEIVED;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeliveryServiceImpl implements DeliveryService {
    private final RestTemplate restTemplate;
    private final DeliveryRepository deliveryRepository;

    @Value("${url.userService}")
    private String urlUserService;

    @Transactional
    @Override
    public void createDelivery(String orderId) {
        Delivery delivery = new Delivery().builder()
                .orderId(UUID.fromString(orderId))
                .created(LocalDateTime.now())
                .changed(LocalDateTime.now())
                .isDeleted(false)
                .status(EnumStatus.CREATED)
                .build();

        Delivery deliverySave = deliveryRepository.save(delivery);

        Long courierId = findCourier(deliverySave.getId());

        delivery.setCourierId(courierId);
        delivery.setStatus(COURIER_FOUND);
        delivery.setChanged(LocalDateTime.now());
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

    @Override
    public EnumStatus getStatus(String uuid) {
        Delivery deliveryPersist = deliveryRepository.findById(UUID.fromString(uuid)).orElseThrow(() ->
                new DeliveryNotFoundException("Id " + uuid + " not found"));
        return deliveryPersist.getStatus();
    }

    private Long findCourier(UUID deliveryUUID) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);
        HttpEntity<UUID> request = new HttpEntity<>(deliveryUUID, headers);

        while (true) {
            ResponseEntity<Long> response = restTemplate.exchange(
                    urlUserService,
                    HttpMethod.POST,
                    request,
                    Long.class
            );

            if (response.getStatusCode().equals(HttpStatusCode.valueOf(302))) {
                return response.getBody();
            } else {
                try {
                    TimeUnit.MINUTES.sleep(1);
                } catch (InterruptedException e) {
                    //  TODO: "Написать лог"
                }
            }

        }


    }
}
