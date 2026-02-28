package org.brightinhere.xyz_retail.infrastructure.persistence.repositories;

import lombok.RequiredArgsConstructor;
import org.brightinhere.xyz_retail.application.port.OrderRepository;
import org.brightinhere.xyz_retail.domain.Order;
import org.brightinhere.xyz_retail.infrastructure.persistence.jpa.OrderSpringDataRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderSpringDataRepository jpa;

    @Override
    public Order save(Order order) {
        return jpa.save(order);
    }

    @Override
    public Optional<Order> findById(UUID orderId) {
        return jpa.findById(orderId);
    }

    @Override
    public Optional<Order> findWithItemsById(UUID orderId) {
        return jpa.findWithItemsById(orderId);
    }
}