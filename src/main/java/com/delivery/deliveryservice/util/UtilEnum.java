package com.delivery.deliveryservice.util;

public enum UtilEnum {
    CREATED("Created"),
    COURIER_FOUND("Courier found"),
    WAITING_COURIER("Waiting courier"),
    SEND_FOR_DELIVERY("Send for delivery"),
    RECEIVED("Received");
    private final String status;

    UtilEnum(String value) {
        this.status = value;
    }

    @Override
    public String toString() {
        return status;
    }

    public static UtilEnum fromValue(String v) {
        for (UtilEnum c : UtilEnum.values()) {
            if (c.status.equals(v)) {
                return c;
            }
        }
        return null;
    }

}
