package com.delivery.deliveryservice.service;

import com.delivery.deliveryservice.dto.request.DeliveryCourierDTO;
import com.delivery.deliveryservice.entity.enumstatus.EnumStatus;

public interface DeliveryService {
    void createDelivery(String orderId);

    void setStatus(DeliveryCourierDTO deliveryCourierDTO);

    EnumStatus getStatus(String uuid);
}
