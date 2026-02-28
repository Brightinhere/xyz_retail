package org.brightinhere.xyz_retail.application.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        String status,
        BigDecimal totalAmount,
        CustomerResponse customer,
        List<OrderItemResponse> items
) {
}