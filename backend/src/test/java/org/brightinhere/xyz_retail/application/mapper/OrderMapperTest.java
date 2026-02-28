package org.brightinhere.xyz_retail.application.mapper;

import org.brightinhere.xyz_retail.application.dto.CustomerResponse;
import org.brightinhere.xyz_retail.application.dto.OrderItemResponse;
import org.brightinhere.xyz_retail.application.dto.OrderResponse;
import org.brightinhere.xyz_retail.domain.Customer;
import org.brightinhere.xyz_retail.domain.Order;
import org.brightinhere.xyz_retail.domain.OrderItem;
import org.brightinhere.xyz_retail.domain.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderMapperTest {

    @Mock
    private CustomerMapper customerMapper;

    @Mock
    private OrderItemMapper orderItemMapper;

    @InjectMocks
    private OrderMapper orderMapper;

    private Order order;
    private Customer customer;
    private OrderItem orderItem;
    private UUID customerId;
    private UUID orderItemId;
    private UUID orderId;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();
        orderItemId = UUID.randomUUID();
        orderId = UUID.randomUUID();

        customer = new Customer();
        customer.setId(customerId);
        customer.setName("John Doe");

        orderItem = new OrderItem();
        orderItem.setId(orderItemId);
        orderItem.setQuantity(2);

        order = new Order();
        order.setId(orderId);
        order.setStatus(OrderStatus.CREATED);
        order.setTotalAmount(new BigDecimal("100.00"));
        order.setCustomer(customer);
        order.setItems(List.of(orderItem));

        CustomerResponse customerResponse = new CustomerResponse(customerId, "John Doe", "1234567890", "john@example.com");

    }

    @Test
    void toResponse_withValidOrder_shouldMapSuccessfully() {
        CustomerResponse customerResponse = new CustomerResponse(customerId, "John Doe", "1234567890", "john@example.com");
        OrderItemResponse itemResponse = new OrderItemResponse(orderItemId, "Product", 2, new BigDecimal("50.00"));

        when(customerMapper.toResponse(customer)).thenReturn(customerResponse);
        when(orderItemMapper.toResponse(orderItem)).thenReturn(itemResponse);

        OrderResponse response = orderMapper.toResponse(order);

        assertNotNull(response);
        assertEquals(orderId, response.id());
        assertEquals("CREATED", response.status());
        assertEquals(new BigDecimal("100.00"), response.totalAmount());
        assertEquals(customerResponse, response.customer());
        assertEquals(1, response.items().size());
        assertEquals(itemResponse, response.items().get(0));
    }

    @Test
    void toResponse_withNullOrder_shouldReturnNull() {
        OrderResponse response = orderMapper.toResponse(null);

        assertNull(response);
    }

    @Test
    void toResponse_withNullItems_shouldReturnEmptyList() {
        order.setItems(null);
        CustomerResponse customerResponse = new CustomerResponse(customerId, "John Doe", "1234567890", "john@example.com");

        when(customerMapper.toResponse(customer)).thenReturn(customerResponse);

        OrderResponse response = orderMapper.toResponse(order);

        assertNotNull(response);
        assertEquals(0, response.items().size());
    }

    @Test
    void toResponse_withEmptyItems_shouldReturnEmptyList() {
        order.setItems(new ArrayList<>());
        CustomerResponse customerResponse = new CustomerResponse(customerId, "John Doe", "1234567890", "john@example.com");

        when(customerMapper.toResponse(customer)).thenReturn(customerResponse);

        OrderResponse response = orderMapper.toResponse(order);

        assertNotNull(response);
        assertEquals(0, response.items().size());
    }

    @Test
    void toResponse_withMultipleItems_shouldMapAllItems() {
        UUID item2Id = UUID.randomUUID();
        OrderItem item2 = new OrderItem();
        item2.setId(item2Id);
        item2.setQuantity(1);

        order.setItems(List.of(orderItem, item2));

        CustomerResponse customerResponse = new CustomerResponse(customerId, "John Doe", "1234567890", "john@example.com");
        OrderItemResponse itemResponse1 = new OrderItemResponse(orderItemId, "Product 1", 2, new BigDecimal("50.00"));
        OrderItemResponse itemResponse2 = new OrderItemResponse(item2Id, "Product 2", 1, new BigDecimal("50.00"));

        when(customerMapper.toResponse(customer)).thenReturn(customerResponse);
        when(orderItemMapper.toResponse(orderItem)).thenReturn(itemResponse1);
        when(orderItemMapper.toResponse(item2)).thenReturn(itemResponse2);

        OrderResponse response = orderMapper.toResponse(order);

        assertNotNull(response);
        assertEquals(2, response.items().size());
    }
}