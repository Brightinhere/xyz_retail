package org.brightinhere.xyz_retail.infrastructure.persistence.repositories;

import lombok.RequiredArgsConstructor;
import org.brightinhere.xyz_retail.application.port.InventoryRepository;
import org.brightinhere.xyz_retail.domain.Inventory;
import org.brightinhere.xyz_retail.infrastructure.persistence.jpa.InventorySpringDataRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class InventoryRepositoryImpl implements InventoryRepository {

    private final InventorySpringDataRepository jpa;

    @Override
    public Inventory save(Inventory inventory) {
        return jpa.save(inventory);
    }

    @Override
    public Optional<Inventory> findByProductId(UUID productId) {
        return jpa.findById(productId);
    }
}