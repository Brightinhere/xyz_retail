package org.brightinhere.xyz_retail.infrastructure.exception;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException() {
        super("Insufficient stock");
    }
}