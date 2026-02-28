package org.brightinhere.xyz_retail.infrastructure.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.brightinhere.xyz_retail.application.dto.request.AddItemRequest;
import org.brightinhere.xyz_retail.application.dto.request.PlaceOrderRequest;
import org.brightinhere.xyz_retail.application.mapper.OrderMapper;
import org.brightinhere.xyz_retail.application.service.OrderService;
import org.brightinhere.xyz_retail.domain.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    /**
     * Create cart / draft order (OrderStatus.CREATED).
     * POST /api/orders
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Object createCart() {
        Order order = orderService.createCart();
        return orderMapper.toResponse(order);
    }

    /**
     * Add product to the order cart with quantity.
     * POST /api/orders/{orderId}/items
     */
    @PostMapping("/{orderId}/items")
    public Object addItem(
            @PathVariable UUID orderId,
            @Valid @RequestBody AddItemRequest request
    ) {
        Order order = orderService.addItem(orderId, request.productId(), request.quantity());
        return orderMapper.toResponse(order);
    }

    /**
     * While placing an order customer name and mobile number is mandatory.
     * POST /api/orders/{orderId}/place
     */
    @PostMapping("/{orderId}/place")
    public Object placeOrder(
            @PathVariable UUID orderId,
            @Valid @RequestBody PlaceOrderRequest request
    ) {
        Order order = orderService.placeOrder(
                orderId,
                request.customerName(),
                request.mobileNumber(),
                request.email()
        );
        return orderMapper.toResponse(order);
    }
}