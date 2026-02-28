package org.brightinhere.xyz_retail.application.mapper;

import org.brightinhere.xyz_retail.application.dto.OrderItemResponse;
import org.brightinhere.xyz_retail.domain.OrderItem;
import org.springframework.stereotype.Component;

@Component
public class OrderItemMapper {

    public OrderItemResponse toResponse(OrderItem item) {
        if (item == null) return null;

        return new OrderItemResponse(
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getQuantity(),
                item.getSalePrice()
        );
    }
}