package org.brightinhere.xyz_retail.application.port;

import org.brightinhere.xyz_retail.domain.Order;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {
    Order save(Order order);

    Optional<Order> findById(UUID orderId);

    Optional<Order> findWithItemsById(UUID orderId);
}