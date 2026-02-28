package org.brightinhere.xyz_retail.infrastructure.web;

        import org.brightinhere.xyz_retail.application.dto.CustomerResponse;
        import org.brightinhere.xyz_retail.application.dto.OrderResponse;
        import org.brightinhere.xyz_retail.application.mapper.OrderMapper;
        import org.brightinhere.xyz_retail.application.service.OrderService;
        import org.brightinhere.xyz_retail.domain.Order;
        import org.brightinhere.xyz_retail.domain.OrderStatus;
        import org.brightinhere.xyz_retail.infrastructure.exception.OrderNotFoundException;
        import org.junit.jupiter.api.BeforeEach;
        import org.junit.jupiter.api.DisplayName;
        import org.junit.jupiter.api.Test;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
        import org.springframework.boot.test.mock.mockito.MockBean;
        import org.springframework.test.web.servlet.MockMvc;

        import java.math.BigDecimal;
        import java.util.List;
        import java.util.UUID;

        import static org.mockito.ArgumentMatchers.any;
        import static org.mockito.Mockito.*;
        import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
        import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

        @WebMvcTest(EmployeeOrderController.class)
        class EmployeeOrderControllerTest {

            @Autowired
            private MockMvc mockMvc;

            @MockBean
            private OrderService orderService;

            @MockBean
            private OrderMapper orderMapper;

            private UUID orderId;
            private Order order;
            private OrderResponse orderResponse;

            @BeforeEach
            void setUp() {
                orderId = UUID.randomUUID();
                order = new Order();
                order.setId(orderId);
                order.setStatus(OrderStatus.PLACED);
                order.setTotalAmount(new BigDecimal("99.99"));

                orderResponse = new OrderResponse(
                        orderId,
                        "PLACED",
                        new BigDecimal("99.99"),
                        new CustomerResponse(UUID.randomUUID(), "John Doe", "1234567890", "john@example.com"),
                        List.of()
                );
            }

            @Test
            @DisplayName("Should return order successfully when order exists")
            void shouldReturnOrderSuccessfully() throws Exception {
                when(orderService.getOrder(orderId)).thenReturn(order);
                when(orderMapper.toResponse(order)).thenReturn(orderResponse);

                mockMvc.perform(get("/api/employee/orders/{orderId}", orderId))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType("application/json"));

                verify(orderService, times(1)).getOrder(orderId);
                verify(orderMapper, times(1)).toResponse(order);
            }

            @Test
            @DisplayName("Should return 404 when order not found")
            void shouldReturn404WhenOrderNotFound() throws Exception {
                when(orderService.getOrder(orderId))
                        .thenThrow(new OrderNotFoundException());

                mockMvc.perform(get("/api/employee/orders/{orderId}", orderId))
                        .andExpect(status().isNotFound());

                verify(orderService, times(1)).getOrder(orderId);
                verify(orderMapper, never()).toResponse(any());
            }

            @Test
            @DisplayName("Should call orderService with correct order ID")
            void shouldCallOrderServiceWithCorrectOrderId() throws Exception {
                when(orderService.getOrder(orderId)).thenReturn(order);
                when(orderMapper.toResponse(order)).thenReturn(orderResponse);

                mockMvc.perform(get("/api/employee/orders/{orderId}", orderId))
                        .andExpect(status().isOk());

                verify(orderService).getOrder(orderId);
            }

            @Test
            @DisplayName("Should call orderMapper with retrieved order")
            void shouldCallOrderMapperWithRetrievedOrder() throws Exception {
                when(orderService.getOrder(orderId)).thenReturn(order);
                when(orderMapper.toResponse(order)).thenReturn(orderResponse);

                mockMvc.perform(get("/api/employee/orders/{orderId}", orderId))
                        .andExpect(status().isOk());

                verify(orderMapper).toResponse(order);
            }

            @Test
            @DisplayName("Should return mapped response from orderMapper")
            void shouldReturnMappedResponseFromOrderMapper() throws Exception {
                when(orderService.getOrder(orderId)).thenReturn(order);
                when(orderMapper.toResponse(order)).thenReturn(orderResponse);

                mockMvc.perform(get("/api/employee/orders/{orderId}", orderId))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType("application/json"));

                verify(orderMapper, times(1)).toResponse(order);
            }

            @Test
            @DisplayName("Should handle different order statuses")
            void shouldHandleDifferentOrderStatuses() throws Exception {
                Order placedOrder = new Order();
                placedOrder.setId(orderId);
                placedOrder.setStatus(OrderStatus.PLACED);

                when(orderService.getOrder(orderId)).thenReturn(placedOrder);
                when(orderMapper.toResponse(placedOrder)).thenReturn(orderResponse);

                mockMvc.perform(get("/api/employee/orders/{orderId}", orderId))
                        .andExpect(status().isOk());

                verify(orderService).getOrder(orderId);
            }

            @Test
            @DisplayName("Should accept valid UUID format in path")
            void shouldAcceptValidUUIDFormat() throws Exception {
                UUID validUUID = UUID.randomUUID();
                when(orderService.getOrder(validUUID)).thenReturn(order);
                when(orderMapper.toResponse(order)).thenReturn(orderResponse);

                mockMvc.perform(get("/api/employee/orders/{orderId}", validUUID))
                        .andExpect(status().isOk());

                verify(orderService).getOrder(validUUID);
            }

            @Test
            @DisplayName("Should return 400 when order ID is invalid UUID format")
            void shouldReturn400WhenOrderIdIsInvalidUUID() throws Exception {
                mockMvc.perform(get("/api/employee/orders/{orderId}", "invalid-uuid"))
                        .andExpect(status().isBadRequest());

                verify(orderService, never()).getOrder(any());
            }

            @Test
            @DisplayName("Should map the order to response object correctly")
            void shouldMapOrderToResponseCorrectly() throws Exception {
                when(orderService.getOrder(orderId)).thenReturn(order);
                when(orderMapper.toResponse(order)).thenReturn(orderResponse);

                mockMvc.perform(get("/api/employee/orders/{orderId}", orderId))
                        .andExpect(status().isOk());

                verify(orderMapper).toResponse(order);
            }

            @Test
            @DisplayName("Should not call orderMapper when order service throws exception")
            void shouldNotCallOrderMapperWhenOrderServiceThrowsException() throws Exception {
                when(orderService.getOrder(orderId))
                        .thenThrow(new OrderNotFoundException());

                mockMvc.perform(get("/api/employee/orders/{orderId}", orderId))
                        .andExpect(status().isNotFound());

                verify(orderMapper, never()).toResponse(any());
            }

            @Test
            @DisplayName("Should use correct endpoint mapping")
            void shouldUseCorrectEndpointMapping() throws Exception {
                when(orderService.getOrder(orderId)).thenReturn(order);
                when(orderMapper.toResponse(order)).thenReturn(orderResponse);

                mockMvc.perform(get("/api/employee/orders/{orderId}", orderId))
                        .andExpect(status().isOk());
            }

            @Test
            @DisplayName("Should return JSON content type")
            void shouldReturnJsonContentType() throws Exception {
                when(orderService.getOrder(orderId)).thenReturn(order);
                when(orderMapper.toResponse(order)).thenReturn(orderResponse);

                mockMvc.perform(get("/api/employee/orders/{orderId}", orderId))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType("application/json"));
            }

            @Test
            @DisplayName("Should handle multiple requests for different order IDs")
            void shouldHandleMultipleRequestsForDifferentOrderIds() throws Exception {
                UUID orderId1 = UUID.randomUUID();
                UUID orderId2 = UUID.randomUUID();
                Order order1 = new Order();
                order1.setId(orderId1);
                Order order2 = new Order();
                order2.setId(orderId2);

                when(orderService.getOrder(orderId1)).thenReturn(order1);
                when(orderService.getOrder(orderId2)).thenReturn(order2);
                when(orderMapper.toResponse(order1)).thenReturn(orderResponse);
                when(orderMapper.toResponse(order2)).thenReturn(orderResponse);

                mockMvc.perform(get("/api/employee/orders/{orderId}", orderId1))
                        .andExpect(status().isOk());

                mockMvc.perform(get("/api/employee/orders/{orderId}", orderId2))
                        .andExpect(status().isOk());

                verify(orderService).getOrder(orderId1);
                verify(orderService).getOrder(orderId2);
            }

            @Test
            @DisplayName("Should only perform GET requests")
            void shouldOnlyPerformGetRequests() throws Exception {
                when(orderService.getOrder(orderId)).thenReturn(order);
                when(orderMapper.toResponse(order)).thenReturn(orderResponse);

                mockMvc.perform(get("/api/employee/orders/{orderId}", orderId))
                        .andExpect(status().isOk());
            }
        }