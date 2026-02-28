package org.brightinhere.xyz_retail.infrastructure.exception;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException() {
        super("Order not found");
    }
}