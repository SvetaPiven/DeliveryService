package com.delivery.deliveryservice.exception;

public class WrongEnumValueException extends RuntimeException{

    public WrongEnumValueException(String message) {
        super(message);
    }
}
