package org.brightinhere.xyz_retail.application.port;

import org.brightinhere.xyz_retail.domain.Inventory;

import java.util.Optional;
import java.util.UUID;

public interface InventoryRepository {
    Inventory save(Inventory inventory);

    Optional<Inventory> findByProductId(UUID productId);
}