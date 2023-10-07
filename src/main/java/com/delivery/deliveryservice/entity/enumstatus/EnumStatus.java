package com.delivery.deliveryservice.entity.enumstatus;

public enum EnumStatus {
    SEARCH_COURIER_FOR_DELIVERY("Search courier for delivery"),
    COURIER_FOUND("Courier found"),
    SEND_FOR_DELIVERY("Send for delivery"),
    RECEIVED("Received");
    private final String status;

    EnumStatus(String value) {
        this.status = value;
    }

    @Override
    public String toString() {
        return status;
    }

    public static EnumStatus fromValue(String v) {
        for (EnumStatus c : EnumStatus.values()) {
            if (c.status.equals(v)) {
                return c;
            }
        }
        return null;
    }

}
