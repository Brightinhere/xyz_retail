package org.brightinhere.xyz_retail.application.mapper;

import lombok.RequiredArgsConstructor;
import org.brightinhere.xyz_retail.application.dto.OrderItemResponse;
import org.brightinhere.xyz_retail.application.dto.OrderResponse;
import org.brightinhere.xyz_retail.domain.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderMapper {

    private final CustomerMapper customerMapper;
    private final OrderItemMapper orderItemMapper;

    public OrderResponse toResponse(Order order) {
        if (order == null) return null;

        List<OrderItemResponse> items = order.getItems() == null
                ? List.of()
                : order.getItems()
                .stream()
                .map(orderItemMapper::toResponse)
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getStatus().name(),
                order.getTotalAmount(),
                customerMapper.toResponse(order.getCustomer()),
                items
        );
    }
}
