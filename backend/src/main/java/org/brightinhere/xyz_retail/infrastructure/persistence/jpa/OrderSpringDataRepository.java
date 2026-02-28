package org.brightinhere.xyz_retail.infrastructure.persistence.jpa;

import org.brightinhere.xyz_retail.domain.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrderSpringDataRepository extends JpaRepository<Order, UUID> {

    @EntityGraph(attributePaths = {"items", "items.product"})
    Optional<Order> findWithItemsById(UUID id);
}