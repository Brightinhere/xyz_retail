package org.brightinhere.xyz_retail.application.dto;

import java.math.BigDecimal;

public record ProductSalesResponse(
        String name,
        BigDecimal price,
        Integer quantityAvailable,
        boolean lowStock,
        Long quantitySold
) {
}