package org.brightinhere.xyz_retail.application.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.brightinhere.xyz_retail.application.port.CustomerRepository;
import org.brightinhere.xyz_retail.application.port.EmailSender;
import org.brightinhere.xyz_retail.application.port.OrderRepository;
import org.brightinhere.xyz_retail.application.port.ProductRepository;
import org.brightinhere.xyz_retail.domain.*;
import org.brightinhere.xyz_retail.infrastructure.exception.InvalidOrderStateException;
import org.brightinhere.xyz_retail.infrastructure.exception.OrderNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final InventoryService inventoryService;
    private final EmailSender emailSender;

    /**
     * Create an empty cart (Order with status CREATED).
     * <p>
     * Rules:
     * - Initializes total amount to zero
     * - No customer assigned until order is placed
     * - Items list is initialized as empty
     */
    @Transactional
    public Order createCart() {
        Order order = new Order();
        order.setStatus(OrderStatus.CREATED);
        order.setItems(new ArrayList<>());
        order.setTotalAmount(BigDecimal.ZERO);
        order.setCustomer(null);
        orderRepository.save(order);

        return order;
    }

    /**
     * Add product to cart with quantity.
     * <p>
     * Rules:
     * - Order must be in CREATED state
     * - Quantity must be positive
     * - If product already exists in cart, update quantity and total price
     * - Recalculate order total after adding/updating item
     */
    @Transactional
    public Order addItem(UUID orderId, UUID productId, int quantity) {
        if (quantity <= 0) {
            throw new InvalidOrderStateException("Quantity must be positive");
        }

        Order order = orderRepository.findWithItemsById(orderId)
                .orElseThrow(OrderNotFoundException::new);

        if (order.getStatus() != OrderStatus.CREATED) {
            throw new InvalidOrderStateException("Cannot modify a finalized order");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(OrderNotFoundException::new);

        if (order.getItems() == null) {
            order.setItems(new ArrayList<>());
        }

        OrderItem existing = order.getItems().stream()
                .filter(i -> Objects.equals(i.getProduct().getId(), productId))
                .findFirst()
                .orElse(null);

        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + quantity);
        } else {
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(quantity);
            item.setSalePrice(product.getPrice());
            order.getItems().add(item);
        }

        recalculateTotal(order);
        orderRepository.save(order);
        return order;
    }

    /**
     * Places the order.
     * <p>
     * Rules:
     * - Order must be in CREATED state
     * - Must contain at least one item
     * - Deducts inventory using optimistic locking
     * - Sends confirmation email after successful transaction
     */
    @Transactional
    public Order placeOrder(UUID orderId, String customerName, String mobileNumber, String email) {

        if (customerName == null || customerName.isBlank()) {
            throw new InvalidOrderStateException("Customer name is required");
        }
        if (mobileNumber == null || mobileNumber.isBlank()) {
            throw new InvalidOrderStateException("Mobile number is required");
        }

        Order order = orderRepository.findWithItemsById(orderId)
                .orElseThrow(OrderNotFoundException::new);

        if (order.getStatus() != OrderStatus.CREATED) {
            throw new InvalidOrderStateException("Order already processed");
        }
        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new InvalidOrderStateException("Cannot place empty order");
        }

        Customer customer = customerRepository.findByMobileNumber(mobileNumber.trim())
                .orElseGet(Customer::new);

        customer.setName(customerName.trim());
        customer.setMobileNumber(mobileNumber.trim());
        customer.setEmail(email == null || email.isBlank() ? null : email.trim());

        customer = customerRepository.save(customer);
        order.setCustomer(customer);

        for (OrderItem item : order.getItems()) {
            inventoryService.deductStock(item.getProduct().getId(), item.getQuantity());
        }

        recalculateTotal(order);

        order.place();

        orderRepository.save(order);
        emailSender.sendOrderConfirmation(
                customer.getName(),
                customer.getEmail(),
                order.getId().toString(),
                String.valueOf(order.getTotalQuantity()),
                order.getTotalAmount().toString()
        );
        return order;
    }

    public Order getOrder(UUID orderId) {
        return orderRepository.findWithItemsById(orderId)
                .orElseThrow(OrderNotFoundException::new);
    }

    private void recalculateTotal(Order order) {
        BigDecimal total = order.getItems().stream()
                .map(i -> i.getSalePrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalAmount(total);
    }
}