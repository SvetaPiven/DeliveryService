package com.delivery.deliveryservice.controller;

import com.delivery.deliveryservice.controller.impl.DeliveryControllerImpl;
import com.delivery.deliveryservice.dto.request.DeliveryCourierDTO;
import com.delivery.deliveryservice.dto.request.DeliveryIdDTO;
import com.delivery.deliveryservice.dto.request.DeliverySetCourierDTO;
import com.delivery.deliveryservice.entity.enumstatus.EnumStatus;
import com.delivery.deliveryservice.exception.CourierNotNeededException;
import com.delivery.deliveryservice.exception.DeletedDeliveryException;
import com.delivery.deliveryservice.exception.DeliveryNotFoundException;
import com.delivery.deliveryservice.exception.WrongEnumValueException;
import com.delivery.deliveryservice.service.DeliveryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DeliveryControllerImpl.class)
class DeliveryControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeliveryService deliveryService;

    @Test
    void testSetStatus_validDto() throws Exception {
        DeliveryCourierDTO deliveryCourierDTO = new DeliveryCourierDTO("c1a1f7cf-9f42-4eb2-af2a-1d07c962f567", "Courier found");

        doNothing().when(deliveryService).setStatus(any(DeliveryCourierDTO.class));

        mockMvc.perform(patch("/api/v1/delivery/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.toJson(deliveryCourierDTO)))
                .andExpect(status().isAccepted());
    }

    @Test
    void testSetStatus_invalidDeliveryId() throws Exception {
        DeliveryCourierDTO deliveryCourierDTO = new DeliveryCourierDTO("12345", "INVALID_STATUS");

        doThrow(NumberFormatException.class)
                .when(deliveryService)
                .setStatus(deliveryCourierDTO);

        mockMvc.perform(patch("/api/v1/delivery/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.toJson(deliveryCourierDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("deliveryId: must be a valid UUID"));
    }

    @Test
    void testSetStatus_invalidStatus() throws Exception {
        DeliveryCourierDTO deliveryCourierDTO = new DeliveryCourierDTO("c1a1f7cf-9f42-4eb2-af2a-1d07c962f567", "INVALID_STATUS");

        doThrow(WrongEnumValueException.class)
                .when(deliveryService)
                .setStatus(deliveryCourierDTO);

        mockMvc.perform(patch("/api/v1/delivery/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.toJson(deliveryCourierDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testSetStatus_deliveryNotFound() throws Exception {
        DeliveryCourierDTO deliveryCourierDTO = new DeliveryCourierDTO("c1a1f7cf-9f42-4eb2-af2a-1d07c962f567", "INVALID_STATUS");

        doThrow(DeliveryNotFoundException.class)
                .when(deliveryService)
                .setStatus(deliveryCourierDTO);

        mockMvc.perform(patch("/api/v1/delivery/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.toJson(deliveryCourierDTO)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testSetStatus_DeletedDelivery() throws Exception {
        DeliveryCourierDTO deliveryCourierDTO = new DeliveryCourierDTO("c1a1f7cf-9f42-4eb2-af2a-1d07c962f567", "INVALID_STATUS");

        doThrow(DeletedDeliveryException.class)
                .when(deliveryService)
                .setStatus(deliveryCourierDTO);

        mockMvc.perform(patch("/api/v1/delivery/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.toJson(deliveryCourierDTO)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testGetStatus_validDeliveryId() throws Exception {
        DeliveryIdDTO deliveryIdDTO = new DeliveryIdDTO("c1a1f7cf-9f42-4eb2-af2a-1d07c962f567");

        EnumStatus expectedStatus = EnumStatus.SEARCH_COURIER_FOR_DELIVERY;
        when(deliveryService.getStatus(deliveryIdDTO.deliveryId())).thenReturn(expectedStatus);

        mockMvc.perform(get("/api/v1/delivery/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.toJson(deliveryIdDTO)))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testGetStatus_invalidDeliveryId() throws Exception {
        DeliveryIdDTO deliveryIdDTO = new DeliveryIdDTO("c1a1f7cf-9f42-4eb2-af2a-1d07c962f567");

        doThrow(NumberFormatException.class)
                .when(deliveryService)
                .getStatus(deliveryIdDTO.deliveryId());

        mockMvc.perform(get("/api/v1/delivery/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.toJson(deliveryIdDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testGetStatus_deletedDelivery() throws Exception {
        DeliveryIdDTO deliveryIdDTO = new DeliveryIdDTO("c1a1f7cf-9f42-4eb2-af2a-1d07c962f567");

        doThrow(DeletedDeliveryException.class)
                .when(deliveryService)
                .getStatus(deliveryIdDTO.deliveryId());

        mockMvc.perform(get("/api/v1/delivery/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.toJson(deliveryIdDTO)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testSetCourier_validDto() throws Exception {
        DeliverySetCourierDTO deliverySetCourierDTO = new DeliverySetCourierDTO("c1a1f7cf-9f42-4eb2-af2a-1d07c962f567", "Courier found");

        doNothing().when(deliveryService).setCourier(any(DeliverySetCourierDTO.class));

        mockMvc.perform(patch("/api/v1/delivery/courier")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.toJson(deliverySetCourierDTO)))
                .andExpect(status().isAccepted());

    }

    @Test
    void testSetCourier_invalidDto() throws Exception {
        DeliverySetCourierDTO deliverySetCourierDTO = new DeliverySetCourierDTO("c1a1f7cf-9f42-4eb2-af2a-1d07c962f567", "134");

        doThrow(NumberFormatException.class)
                .when(deliveryService)
                .setCourier(deliverySetCourierDTO);

        mockMvc.perform(patch("/api/v1/delivery/courier")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.toJson(deliverySetCourierDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testSetCourier_deliveryDoesntExists() throws Exception {
        DeliverySetCourierDTO deliverySetCourierDTO = new DeliverySetCourierDTO("c1a1f7cf-9f42-4eb2-af2a-1d07c962f567", "134");

        doThrow(DeliveryNotFoundException.class)
                .when(deliveryService)
                .setCourier(deliverySetCourierDTO);

        mockMvc.perform(patch("/api/v1/delivery/courier")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.toJson(deliverySetCourierDTO)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testSetCourier_courierAlreadyExists() throws Exception {
        DeliverySetCourierDTO deliverySetCourierDTO = new DeliverySetCourierDTO("c1a1f7cf-9f42-4eb2-af2a-1d07c962f567", "134");

        doThrow(CourierNotNeededException.class)
                .when(deliveryService)
                .setCourier(deliverySetCourierDTO);

        mockMvc.perform(patch("/api/v1/delivery/courier")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.toJson(deliverySetCourierDTO)))
                .andExpect(status().isIAmATeapot())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}