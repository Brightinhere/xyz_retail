package org.brightinhere.xyz_retail.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemResponse(
        UUID productId,
        String productName,
        Integer quantity,
        BigDecimal salePrice
) {
}