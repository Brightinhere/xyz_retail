package org.brightinhere.xyz_retail.infrastructure.exception;

public class InventoryNotFoundException extends RuntimeException {
    public InventoryNotFoundException() {
        super("Inventory not found");
    }
}