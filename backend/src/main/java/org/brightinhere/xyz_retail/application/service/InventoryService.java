package org.brightinhere.xyz_retail.application.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.brightinhere.xyz_retail.application.port.InventoryRepository;
import org.brightinhere.xyz_retail.domain.Inventory;
import org.brightinhere.xyz_retail.infrastructure.exception.InventoryNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public Optional<Inventory> findByProductId(UUID productId) {
        return inventoryRepository.findByProductId(productId);
    }

    public void save(Inventory inventory) {
        inventoryRepository.save(inventory);
    }

    @Transactional
    public void deductStock(UUID productId, int qty) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(InventoryNotFoundException::new);

        inventory.deduct(qty);
        inventoryRepository.save(inventory);
    }
}