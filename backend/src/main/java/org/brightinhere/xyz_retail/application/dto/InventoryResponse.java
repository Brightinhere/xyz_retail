package org.brightinhere.xyz_retail.application.dto;

public record InventoryResponse(
        Integer quantity,
        boolean lowStock
) {
}