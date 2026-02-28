package org.brightinhere.xyz_retail.infrastructure.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.brightinhere.xyz_retail.application.dto.CustomerResponse;
import org.brightinhere.xyz_retail.application.dto.OrderItemResponse;
import org.brightinhere.xyz_retail.application.dto.OrderResponse;
import org.brightinhere.xyz_retail.application.dto.request.AddItemRequest;
import org.brightinhere.xyz_retail.application.dto.request.PlaceOrderRequest;
import org.brightinhere.xyz_retail.application.mapper.OrderMapper;
import org.brightinhere.xyz_retail.application.service.OrderService;
import org.brightinhere.xyz_retail.domain.Order;
import org.brightinhere.xyz_retail.domain.OrderStatus;
import org.brightinhere.xyz_retail.infrastructure.exception.InvalidOrderStateException;
import org.brightinhere.xyz_retail.infrastructure.exception.OrderNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    @MockBean
    private OrderMapper orderMapper;

    @Nested
    @DisplayName("POST /api/orders")
    class CreateCart {

        @Test
        @DisplayName("Should create cart and return 201 Created")
        void shouldCreateCartAndReturn201() throws Exception {
            Order order = new Order();
            UUID orderId = UUID.randomUUID();
            order.setId(orderId);
            order.setStatus(OrderStatus.CREATED);
            order.setTotalAmount(BigDecimal.ZERO);

            OrderResponse response = new OrderResponse(orderId, "CREATED", BigDecimal.ZERO, null, List.of());

            when(orderService.createCart()).thenReturn(order);
            when(orderMapper.toResponse(order)).thenReturn(response);

            mockMvc.perform(post("/api/orders")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(orderId.toString()))
                    .andExpect(jsonPath("$.status").value("CREATED"))
                    .andExpect(jsonPath("$.totalAmount").value(0))
                    .andExpect(jsonPath("$.items").isEmpty());

            verify(orderService).createCart();
            verify(orderMapper).toResponse(order);
        }

        @Test
        @DisplayName("Should call orderService.createCart exactly once")
        void shouldCallCreateCartOnce() throws Exception {
            Order order = new Order();
            order.setId(UUID.randomUUID());
            OrderResponse response = new OrderResponse(order.getId(), "CREATED", BigDecimal.ZERO, null, List.of());

            when(orderService.createCart()).thenReturn(order);
            when(orderMapper.toResponse(order)).thenReturn(response);

            mockMvc.perform(post("/api/orders"))
                    .andExpect(status().isCreated());

            verify(orderService, times(1)).createCart();
        }

        @Test
        @DisplayName("Should return JSON content type")
        void shouldReturnJsonContentType() throws Exception {
            Order order = new Order();
            order.setId(UUID.randomUUID());
            OrderResponse response = new OrderResponse(order.getId(), "CREATED", BigDecimal.ZERO, null, List.of());

            when(orderService.createCart()).thenReturn(order);
            when(orderMapper.toResponse(order)).thenReturn(response);

            mockMvc.perform(post("/api/orders")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }
    }

    @Nested
    @DisplayName("POST /api/orders/{orderId}/items")
    class AddItem {

        @Test
        @DisplayName("Should add item to order and return 200")
        void shouldAddItemAndReturn200() throws Exception {
            UUID orderId = UUID.randomUUID();
            UUID productId = UUID.randomUUID();
            int quantity = 3;

            Order order = new Order();
            order.setId(orderId);
            order.setStatus(OrderStatus.CREATED);
            order.setTotalAmount(new BigDecimal("30.00"));

            OrderItemResponse itemResponse = new OrderItemResponse(productId, "Test Product", quantity, new BigDecimal("10.00"));
            OrderResponse response = new OrderResponse(orderId, "CREATED", new BigDecimal("30.00"), null, List.of(itemResponse));

            when(orderService.addItem(orderId, productId, quantity)).thenReturn(order);
            when(orderMapper.toResponse(order)).thenReturn(response);

            AddItemRequest request = new AddItemRequest(productId, quantity);

            mockMvc.perform(post("/api/orders/{orderId}/items", orderId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(orderId.toString()))
                    .andExpect(jsonPath("$.status").value("CREATED"))
                    .andExpect(jsonPath("$.totalAmount").value(30.00))
                    .andExpect(jsonPath("$.items", org.hamcrest.Matchers.hasSize(1)))
                    .andExpect(jsonPath("$.items[0].productId").value(productId.toString()))
                    .andExpect(jsonPath("$.items[0].productName").value("Test Product"))
                    .andExpect(jsonPath("$.items[0].quantity").value(3));

            verify(orderService).addItem(orderId, productId, quantity);
            verify(orderMapper).toResponse(order);
        }

        @Test
        @DisplayName("Should return 404 when order not found")
        void shouldReturn404WhenOrderNotFound() throws Exception {
            UUID orderId = UUID.randomUUID();
            UUID productId = UUID.randomUUID();

            when(orderService.addItem(eq(orderId), eq(productId), eq(2)))
                    .thenThrow(new OrderNotFoundException());

            AddItemRequest request = new AddItemRequest(productId, 2);

            mockMvc.perform(post("/api/orders/{orderId}/items", orderId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());

            verify(orderService).addItem(orderId, productId, 2);
            verify(orderMapper, never()).toResponse(any());
        }

        @Test
        @DisplayName("Should return 409 when order is finalized")
        void shouldReturn409WhenOrderFinalized() throws Exception {
            UUID orderId = UUID.randomUUID();
            UUID productId = UUID.randomUUID();

            when(orderService.addItem(eq(orderId), eq(productId), eq(1)))
                    .thenThrow(new InvalidOrderStateException("Cannot modify a finalized order"));

            AddItemRequest request = new AddItemRequest(productId, 1);

            mockMvc.perform(post("/api/orders/{orderId}/items", orderId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict());

            verify(orderMapper, never()).toResponse(any());
        }

        @Test
        @DisplayName("Should return 400 when productId is null")
        void shouldReturn400WhenProductIdNull() throws Exception {
            UUID orderId = UUID.randomUUID();

            String json = "{\"productId\": null, \"quantity\": 1}";

            mockMvc.perform(post("/api/orders/{orderId}/items", orderId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest());

            verify(orderService, never()).addItem(any(), any(), anyInt());
        }

        @Test
        @DisplayName("Should return 400 when quantity is null")
        void shouldReturn400WhenQuantityNull() throws Exception {
            UUID orderId = UUID.randomUUID();
            UUID productId = UUID.randomUUID();

            String json = "{\"productId\": \"" + productId + "\", \"quantity\": null}";

            mockMvc.perform(post("/api/orders/{orderId}/items", orderId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest());

            verify(orderService, never()).addItem(any(), any(), anyInt());
        }

        @Test
        @DisplayName("Should return 400 when quantity is zero")
        void shouldReturn400WhenQuantityZero() throws Exception {
            UUID orderId = UUID.randomUUID();
            UUID productId = UUID.randomUUID();

            AddItemRequest request = new AddItemRequest(productId, 0);

            mockMvc.perform(post("/api/orders/{orderId}/items", orderId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(orderService, never()).addItem(any(), any(), anyInt());
        }

        @Test
        @DisplayName("Should return 400 when quantity is negative")
        void shouldReturn400WhenQuantityNegative() throws Exception {
            UUID orderId = UUID.randomUUID();
            UUID productId = UUID.randomUUID();

            AddItemRequest request = new AddItemRequest(productId, -5);

            mockMvc.perform(post("/api/orders/{orderId}/items", orderId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(orderService, never()).addItem(any(), any(), anyInt());
        }

        @Test
        @DisplayName("Should return 400 when request body is missing")
        void shouldReturn400WhenBodyMissing() throws Exception {
            UUID orderId = UUID.randomUUID();

            mockMvc.perform(post("/api/orders/{orderId}/items", orderId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(orderService, never()).addItem(any(), any(), anyInt());
        }

        @Test
        @DisplayName("Should return 400 when orderId is invalid UUID")
        void shouldReturn400WhenOrderIdInvalid() throws Exception {
            String json = "{\"productId\": \"" + UUID.randomUUID() + "\", \"quantity\": 1}";

            mockMvc.perform(post("/api/orders/{orderId}/items", "not-a-uuid")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest());

            verify(orderService, never()).addItem(any(), any(), anyInt());
        }
    }

    @Nested
    @DisplayName("POST /api/orders/{orderId}/place")
    class PlaceOrder {

        @Test
        @DisplayName("Should place order and return 200 with all fields")
        void shouldPlaceOrderAndReturn200() throws Exception {
            UUID orderId = UUID.randomUUID();
            UUID customerId = UUID.randomUUID();

            Order order = new Order();
            order.setId(orderId);
            order.setStatus(OrderStatus.PLACED);
            order.setTotalAmount(new BigDecimal("150.00"));

            CustomerResponse customerResponse = new CustomerResponse(customerId, "John Doe", "1234567890", "john@example.com");
            OrderResponse response = new OrderResponse(orderId, "PLACED", new BigDecimal("150.00"), customerResponse, List.of());

            when(orderService.placeOrder(orderId, "John Doe", "1234567890", "john@example.com")).thenReturn(order);
            when(orderMapper.toResponse(order)).thenReturn(response);

            PlaceOrderRequest request = new PlaceOrderRequest("John Doe", "1234567890", "john@example.com");

            mockMvc.perform(post("/api/orders/{orderId}/place", orderId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(orderId.toString()))
                    .andExpect(jsonPath("$.status").value("PLACED"))
                    .andExpect(jsonPath("$.totalAmount").value(150.00))
                    .andExpect(jsonPath("$.customer.name").value("John Doe"))
                    .andExpect(jsonPath("$.customer.mobileNumber").value("1234567890"))
                    .andExpect(jsonPath("$.customer.email").value("john@example.com"));

            verify(orderService).placeOrder(orderId, "John Doe", "1234567890", "john@example.com");
            verify(orderMapper).toResponse(order);
        }

        @Test
        @DisplayName("Should place order without email (email is optional)")
        void shouldPlaceOrderWithoutEmail() throws Exception {
            UUID orderId = UUID.randomUUID();

            Order order = new Order();
            order.setId(orderId);
            order.setStatus(OrderStatus.PLACED);
            order.setTotalAmount(new BigDecimal("50.00"));

            OrderResponse response = new OrderResponse(orderId, "PLACED", new BigDecimal("50.00"), null, List.of());

            when(orderService.placeOrder(orderId, "Jane Doe", "0987654321", null)).thenReturn(order);
            when(orderMapper.toResponse(order)).thenReturn(response);

            PlaceOrderRequest request = new PlaceOrderRequest("Jane Doe", "0987654321", null);

            mockMvc.perform(post("/api/orders/{orderId}/place", orderId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(orderId.toString()))
                    .andExpect(jsonPath("$.status").value("PLACED"));

            verify(orderService).placeOrder(orderId, "Jane Doe", "0987654321", null);
        }

        @Test
        @DisplayName("Should return 404 when order not found")
        void shouldReturn404WhenOrderNotFound() throws Exception {
            UUID orderId = UUID.randomUUID();

            when(orderService.placeOrder(eq(orderId), anyString(), anyString(), any()))
                    .thenThrow(new OrderNotFoundException());

            PlaceOrderRequest request = new PlaceOrderRequest("John Doe", "1234567890", null);

            mockMvc.perform(post("/api/orders/{orderId}/place", orderId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());

            verify(orderMapper, never()).toResponse(any());
        }

        @Test
        @DisplayName("Should return 409 when order already processed")
        void shouldReturn409WhenOrderAlreadyProcessed() throws Exception {
            UUID orderId = UUID.randomUUID();

            when(orderService.placeOrder(eq(orderId), anyString(), anyString(), any()))
                    .thenThrow(new InvalidOrderStateException("Order already processed"));

            PlaceOrderRequest request = new PlaceOrderRequest("John Doe", "1234567890", null);

            mockMvc.perform(post("/api/orders/{orderId}/place", orderId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict());

            verify(orderMapper, never()).toResponse(any());
        }

        @Test
        @DisplayName("Should return 400 when customerName is blank")
        void shouldReturn400WhenCustomerNameBlank() throws Exception {
            UUID orderId = UUID.randomUUID();

            PlaceOrderRequest request = new PlaceOrderRequest("", "1234567890", null);

            mockMvc.perform(post("/api/orders/{orderId}/place", orderId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(orderService, never()).placeOrder(any(), anyString(), anyString(), any());
        }

        @Test
        @DisplayName("Should return 400 when mobileNumber is blank")
        void shouldReturn400WhenMobileNumberBlank() throws Exception {
            UUID orderId = UUID.randomUUID();

            PlaceOrderRequest request = new PlaceOrderRequest("John Doe", "", null);

            mockMvc.perform(post("/api/orders/{orderId}/place", orderId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(orderService, never()).placeOrder(any(), anyString(), anyString(), any());
        }

        @Test
        @DisplayName("Should return 400 when customerName is missing")
        void shouldReturn400WhenCustomerNameMissing() throws Exception {
            UUID orderId = UUID.randomUUID();

            String json = "{\"mobileNumber\": \"1234567890\"}";

            mockMvc.perform(post("/api/orders/{orderId}/place", orderId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest());

            verify(orderService, never()).placeOrder(any(), anyString(), anyString(), any());
        }

        @Test
        @DisplayName("Should return 400 when mobileNumber is missing")
        void shouldReturn400WhenMobileNumberMissing() throws Exception {
            UUID orderId = UUID.randomUUID();

            String json = "{\"customerName\": \"John Doe\"}";

            mockMvc.perform(post("/api/orders/{orderId}/place", orderId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest());

            verify(orderService, never()).placeOrder(any(), anyString(), anyString(), any());
        }

        @Test
        @DisplayName("Should return 400 when request body is missing")
        void shouldReturn400WhenBodyMissing() throws Exception {
            UUID orderId = UUID.randomUUID();

            mockMvc.perform(post("/api/orders/{orderId}/place", orderId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(orderService, never()).placeOrder(any(), anyString(), anyString(), any());
        }

        @Test
        @DisplayName("Should return 400 when orderId is invalid UUID")
        void shouldReturn400WhenOrderIdInvalid() throws Exception {
            PlaceOrderRequest request = new PlaceOrderRequest("John Doe", "1234567890", null);

            mockMvc.perform(post("/api/orders/{orderId}/place", "not-a-uuid")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(orderService, never()).placeOrder(any(), anyString(), anyString(), any());
        }
    }
}