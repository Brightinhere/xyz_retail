package org.brightinhere.xyz_retail.infrastructure.exception;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException() {
        super("Product not found");
    }
}