package com.delivery.deliveryservice.service;

import java.util.UUID;

public interface DeliveryService {
     void createDelivery(String orderId);

     void setStatus(UUID uuid, String status);

     String getStatus(UUID uuid);
}
