package org.brightinhere.xyz_retail.application.service;

import org.brightinhere.xyz_retail.application.port.CustomerRepository;
import org.brightinhere.xyz_retail.application.port.EmailSender;
import org.brightinhere.xyz_retail.application.port.OrderRepository;
import org.brightinhere.xyz_retail.application.port.ProductRepository;
import org.brightinhere.xyz_retail.domain.*;
import org.brightinhere.xyz_retail.infrastructure.exception.InvalidOrderStateException;
import org.brightinhere.xyz_retail.infrastructure.exception.OrderNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private InventoryService inventoryService;

    @Mock
    private EmailSender emailSender;

    @InjectMocks
    private OrderService orderService;

    private UUID orderId;
    private UUID productId;
    private UUID customerId;
    private Order order;
    private Product product;
    private Customer customer;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
        productId = UUID.randomUUID();
        customerId = UUID.randomUUID();

        order = new Order();
        order.setId(orderId);
        order.setStatus(OrderStatus.CREATED);
        order.setItems(new ArrayList<>());
        order.setTotalAmount(BigDecimal.ZERO);

        product = new Product();
        product.setId(productId);
        product.setPrice(new BigDecimal("50.00"));

        customer = new Customer();
        customer.setId(customerId);
        customer.setName("John Doe");
        customer.setMobileNumber("1234567890");
        customer.setEmail("john@example.com");
    }

    @Test
    void createCart_successfullyCreatesEmptyOrder() {
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order result = orderService.createCart();

        assertNotNull(result);
        assertEquals(OrderStatus.CREATED, result.getStatus());
        assertTrue(result.getItems().isEmpty());
        assertEquals(BigDecimal.ZERO, result.getTotalAmount());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void addItem_successfullyAddsProductToOrder() {
        order.setItems(new ArrayList<>());
        when(orderRepository.findWithItemsById(orderId)).thenReturn(Optional.of(order));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order result = orderService.addItem(orderId, productId, 5);

        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals(5, result.getItems().get(0).getQuantity());
        verify(orderRepository, times(1)).findWithItemsById(orderId);
        verify(productRepository, times(1)).findById(productId);
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void addItem_whenQuantityIsZero_throwsException() {
        assertThrows(InvalidOrderStateException.class,
                () -> orderService.addItem(orderId, productId, 0));

        verify(orderRepository, never()).findWithItemsById(any());
    }

    @Test
    void addItem_whenOrderNotFound_throwsException() {
        when(orderRepository.findWithItemsById(orderId)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class,
                () -> orderService.addItem(orderId, productId, 5));

        verify(orderRepository, times(1)).findWithItemsById(orderId);
    }

    @Test
    void addItem_whenProductNotFound_throwsException() {
        when(orderRepository.findWithItemsById(orderId)).thenReturn(Optional.of(order));
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class,
                () -> orderService.addItem(orderId, productId, 5));

        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    void addItem_whenOrderIsFinalized_throwsException() {
        order.setStatus(OrderStatus.PLACED);
        when(orderRepository.findWithItemsById(orderId)).thenReturn(Optional.of(order));

        assertThrows(InvalidOrderStateException.class,
                () -> orderService.addItem(orderId, productId, 5));

        verify(orderRepository, never()).save(any());
    }

    @Test
    void addItem_whenProductAlreadyExists_incrementsQuantity() {
        OrderItem existingItem = new OrderItem();
        existingItem.setProduct(product);
        existingItem.setQuantity(3);
        existingItem.setSalePrice(product.getPrice());
        order.getItems().add(existingItem);

        when(orderRepository.findWithItemsById(orderId)).thenReturn(Optional.of(order));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        orderService.addItem(orderId, productId, 2);

        assertEquals(5, existingItem.getQuantity());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void placeOrder_successfullyPlacesOrder() {
        OrderItem item = new OrderItem();
        item.setProduct(product);
        item.setQuantity(2);
        item.setSalePrice(product.getPrice());
        order.getItems().add(item);

        when(orderRepository.findWithItemsById(orderId)).thenReturn(Optional.of(order));
        when(customerRepository.findByMobileNumber("1234567890")).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order result = orderService.placeOrder(orderId, "John Doe", "1234567890", "john@example.com");

        assertNotNull(result);
        assertEquals(OrderStatus.PLACED, result.getStatus());
        verify(inventoryService, times(1)).deductStock(productId, 2);
        verify(emailSender, times(1)).sendOrderConfirmation(any(), any(), any(), any(), any());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void placeOrder_whenCustomerNameIsEmpty_throwsException() {
        assertThrows(InvalidOrderStateException.class,
                () -> orderService.placeOrder(orderId, "", "1234567890", "john@example.com"));

        verify(orderRepository, never()).save(any());
    }

    @Test
    void placeOrder_whenMobileNumberIsEmpty_throwsException() {
        assertThrows(InvalidOrderStateException.class,
                () -> orderService.placeOrder(orderId, "John Doe", "", "john@example.com"));

        verify(orderRepository, never()).save(any());
    }

    @Test
    void placeOrder_whenOrderNotFound_throwsException() {
        when(orderRepository.findWithItemsById(orderId)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class,
                () -> orderService.placeOrder(orderId, "John Doe", "1234567890", "john@example.com"));
    }

    @Test
    void placeOrder_whenOrderAlreadyProcessed_throwsException() {
        order.setStatus(OrderStatus.PLACED);
        when(orderRepository.findWithItemsById(orderId)).thenReturn(Optional.of(order));

        assertThrows(InvalidOrderStateException.class,
                () -> orderService.placeOrder(orderId, "John Doe", "1234567890", "john@example.com"));
    }

    @Test
    void placeOrder_whenOrderIsEmpty_throwsException() {
        order.setItems(new ArrayList<>());
        when(orderRepository.findWithItemsById(orderId)).thenReturn(Optional.of(order));

        assertThrows(InvalidOrderStateException.class,
                () -> orderService.placeOrder(orderId, "John Doe", "1234567890", "john@example.com"));
    }

    @Test
    void getOrder_whenExists_returnsOrder() {
        when(orderRepository.findWithItemsById(orderId)).thenReturn(Optional.of(order));

        Order result = orderService.getOrder(orderId);

        assertNotNull(result);
        assertEquals(orderId, result.getId());
        verify(orderRepository, times(1)).findWithItemsById(orderId);
    }

    @Test
    void getOrder_whenNotFound_throwsException() {
        when(orderRepository.findWithItemsById(orderId)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class,
                () -> orderService.getOrder(orderId));
    }
}