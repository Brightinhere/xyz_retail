package org.brightinhere.xyz_retail.domain;

import org.brightinhere.xyz_retail.infrastructure.exception.InvalidOrderStateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    private Order order;
    private Customer customer;

    @BeforeEach
    void setUp() {
        order = new Order();
        order.setId(UUID.randomUUID());
        order.setItems(new ArrayList<>());
        order.setTotalAmount(BigDecimal.ZERO);
        order.setStatus(OrderStatus.CREATED);

        customer = new Customer();
        customer.setId(UUID.randomUUID());
        order.setCustomer(customer);
    }

    @Test
    void getTotalQuantity_withSingleItem_returnsThatQuantity() {
        OrderItem item = createOrderItem(5);
        order.getItems().add(item);

        assertEquals(5, order.getTotalQuantity());
    }

    @Test
    void getTotalQuantity_withMultipleItems_returnsSumOfAllQuantities() {
        order.getItems().add(createOrderItem(5));
        order.getItems().add(createOrderItem(10));
        order.getItems().add(createOrderItem(3));

        assertEquals(18, order.getTotalQuantity());
    }

    @Test
    void getTotalQuantity_withNoItems_returnsZero() {
        assertEquals(0, order.getTotalQuantity());
    }

    @Test
    void getTotalQuantity_withLargeQuantities_returnCorrectSum() {
        order.getItems().add(createOrderItem(1000));
        order.getItems().add(createOrderItem(2000));
        order.getItems().add(createOrderItem(500));

        assertEquals(3500, order.getTotalQuantity());
    }

    @Test
    void getTotalQuantity_withSingleItemQuantityOne_returnsOne() {
        order.getItems().add(createOrderItem(1));

        assertEquals(1, order.getTotalQuantity());
    }

    @Test
    void place_withValidOrder_setsStatusToPlaced() {
        order.getItems().add(createOrderItem(5));

        order.place();

        assertEquals(OrderStatus.PLACED, order.getStatus());
    }

    @Test
    void place_withValidOrderMultipleItems_setsStatusToPlaced() {
        order.getItems().add(createOrderItem(5));
        order.getItems().add(createOrderItem(10));

        order.place();

        assertEquals(OrderStatus.PLACED, order.getStatus());
    }

    @Test
    void place_withEmptyOrder_throwsInvalidOrderStateException() {
        assertThrows(InvalidOrderStateException.class, () -> order.place());
        assertEquals(OrderStatus.CREATED, order.getStatus());
    }

    @Test
    void place_withEmptyOrderErrorMessage_containsExpectedText() {
        InvalidOrderStateException exception = assertThrows(
                InvalidOrderStateException.class,
                () -> order.place()
        );

        assertTrue(exception.getMessage().contains("Cannot place empty order"));
    }

    @Test
    void place_withOrderAlreadyPlaced_throwsInvalidOrderStateException() {
        order.getItems().add(createOrderItem(5));
        order.setStatus(OrderStatus.PLACED);

        assertThrows(InvalidOrderStateException.class, () -> order.place());
        assertEquals(OrderStatus.PLACED, order.getStatus());
    }

    @Test
    void place_withOrderAlreadyCancelled_throwsInvalidOrderStateException() {
        order.getItems().add(createOrderItem(5));
        order.setStatus(OrderStatus.CANCELLED);

        assertThrows(InvalidOrderStateException.class, () -> order.place());
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    void place_withOrderAlreadyProcessedErrorMessage_containsExpectedText() {
        order.getItems().add(createOrderItem(5));
        order.setStatus(OrderStatus.PLACED);

        InvalidOrderStateException exception = assertThrows(
                InvalidOrderStateException.class,
                () -> order.place()
        );

        assertTrue(exception.getMessage().contains("Order already processed"));
    }

    @Test
    void place_doesNotModifyOrderId() {
        UUID originalId = order.getId();
        order.getItems().add(createOrderItem(5));

        order.place();

        assertEquals(originalId, order.getId());
    }

    @Test
    void place_doesNotModifyTotalAmount() {
        BigDecimal originalAmount = new BigDecimal("100.00");
        order.setTotalAmount(originalAmount);
        order.getItems().add(createOrderItem(5));

        order.place();

        assertEquals(originalAmount, order.getTotalAmount());
    }

    @Test
    void place_doesNotModifyCustomer() {
        UUID originalCustomerId = customer.getId();
        order.getItems().add(createOrderItem(5));

        order.place();

        assertEquals(originalCustomerId, order.getCustomer().getId());
    }

    private OrderItem createOrderItem(int quantity) {
        OrderItem item = new OrderItem();
        item.setQuantity(quantity);
        return item;
    }
}