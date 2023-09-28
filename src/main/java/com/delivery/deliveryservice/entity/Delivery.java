package com.delivery.deliveryservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "deliveries")
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "courier_id")
    private Long courierId;

    @Size(max = 50)
    @NotNull
    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @NotNull
    @Column(name = "created", nullable = false)
    private Instant created;

    @Column(name = "changed")
    private Instant changed;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

}