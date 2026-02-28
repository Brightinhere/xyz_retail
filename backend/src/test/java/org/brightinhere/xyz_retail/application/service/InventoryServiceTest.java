package org.brightinhere.xyz_retail.application.service;

import org.brightinhere.xyz_retail.application.port.InventoryRepository;
import org.brightinhere.xyz_retail.domain.Inventory;
import org.brightinhere.xyz_retail.infrastructure.exception.InventoryNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private InventoryService inventoryService;

    private UUID productId;
    private Inventory inventory;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();
        inventory = new Inventory();
        inventory.setProductId(productId);
        inventory.setQuantity(100);
    }

    @Test
    void findByProductId_whenExists_returnsInventory() {
        when(inventoryRepository.findByProductId(productId))
                .thenReturn(Optional.of(inventory));

        Optional<Inventory> result = inventoryService.findByProductId(productId);

        assertTrue(result.isPresent());
        assertEquals(inventory, result.get());
        verify(inventoryRepository, times(1)).findByProductId(productId);
    }

    @Test
    void findByProductId_whenNotExists_returnsEmpty() {
        when(inventoryRepository.findByProductId(productId))
                .thenReturn(Optional.empty());

        Optional<Inventory> result = inventoryService.findByProductId(productId);

        assertFalse(result.isPresent());
        verify(inventoryRepository, times(1)).findByProductId(productId);
    }

    @Test
    void save_persistsInventory() {
        inventoryService.save(inventory);

        verify(inventoryRepository, times(1)).save(inventory);
    }

    @Test
    void deductStock_successfullyReducesQuantity() {
        int quantityToDeduct = 30;
        when(inventoryRepository.findByProductId(productId))
                .thenReturn(Optional.of(inventory));

        inventoryService.deductStock(productId, quantityToDeduct);

        verify(inventoryRepository, times(1)).findByProductId(productId);
        verify(inventoryRepository, times(1)).save(inventory);
    }

    @Test
    void deductStock_whenProductNotFound_throwsException() {
        when(inventoryRepository.findByProductId(productId))
                .thenReturn(Optional.empty());

        assertThrows(InventoryNotFoundException.class,
                () -> inventoryService.deductStock(productId, 30));

        verify(inventoryRepository, times(1)).findByProductId(productId);
        verify(inventoryRepository, never()).save(any());
    }
}