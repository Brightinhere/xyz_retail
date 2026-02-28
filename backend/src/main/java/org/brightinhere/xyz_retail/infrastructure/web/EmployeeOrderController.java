package org.brightinhere.xyz_retail.infrastructure.web;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.brightinhere.xyz_retail.application.mapper.OrderMapper;
import org.brightinhere.xyz_retail.application.service.OrderService;
import org.brightinhere.xyz_retail.domain.Order;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/employee/orders")
public class EmployeeOrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @GetMapping("/{orderId}")
    public Object getOrderById(@PathVariable @NotNull UUID orderId) {
        Order order = orderService.getOrder(orderId);
        return orderMapper.toResponse(order);
    }
}