package com.delivery.deliveryservice.service.impl;

import com.delivery.deliveryservice.entity.Delivery;
import com.delivery.deliveryservice.repository.DeliveryRepository;
import com.delivery.deliveryservice.service.DeliveryService;
import com.delivery.deliveryservice.util.UtilEnum;
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
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.delivery.deliveryservice.util.UtilEnum.COURIER_FOUND;
import static com.delivery.deliveryservice.util.UtilEnum.RECEIVED;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.I_AM_A_TEAPOT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
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
    public void createDelivery(String orderId) {
        Delivery delivery = new Delivery();
        delivery.setOrderId(UUID.fromString(orderId));
        delivery.setCreated(LocalDateTime.now());
        delivery.setChanged(LocalDateTime.now());
        delivery.setIsDeleted(false);
        delivery.setStatus(UtilEnum.CREATED.toString());

        Delivery deliverySave = deliveryRepository.save(delivery);

        Long courierId = findCourier(deliverySave.getId());

        delivery.setCourierId(courierId);
        delivery.setStatus(COURIER_FOUND.toString());
        delivery.setChanged(LocalDateTime.now());
    }

    @Transactional
    @Override
    public void setStatus(UUID uuid, String status) {
        Delivery deliveryPersist = deliveryRepository.findById(uuid).orElseThrow(() ->
                new ResponseStatusException((NOT_FOUND), "Id " + uuid + " not found"));

        if (Boolean.TRUE.equals(deliveryPersist.getIsDeleted()))
            throw new ResponseStatusException((BAD_REQUEST), "Delivery is deleted");

        if (UtilEnum.fromValue(status) == null)
            throw new ResponseStatusException((I_AM_A_TEAPOT), "Status " +
                    status + " not found in UtilEnum");

        deliveryPersist.setStatus(status);
        deliveryPersist.setChanged(LocalDateTime.now());

        if (UtilEnum.fromValue(status) == RECEIVED)
            deliveryPersist.setIsDeleted(true);

    }

    @Override
    public String getStatus(UUID uuid) {
        Delivery deliveryPersist = deliveryRepository.findById(uuid).orElseThrow(() ->
                new ResponseStatusException((NOT_FOUND), "Id " + uuid + " not found"));
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
