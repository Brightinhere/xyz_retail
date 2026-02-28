package org.brightinhere.xyz_retail.domain;

import org.brightinhere.xyz_retail.infrastructure.exception.InsufficientStockException;
import org.brightinhere.xyz_retail.infrastructure.exception.InvalidOrderStateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class InventoryTest {

    private Inventory inventory;

    @BeforeEach
    void setUp() {
        inventory = new Inventory();
        inventory.setProductId(UUID.randomUUID());
        inventory.setQuantity(100);
        inventory.setVersion(0L);
    }

    @Test
    void deduct_withValidAmount_decreasesQuantity() {
        int initialQuantity = inventory.getQuantity();
        int deductAmount = 10;

        inventory.deduct(deductAmount);

        assertEquals(initialQuantity - deductAmount, inventory.getQuantity());
    }

    @Test
    void deduct_withExactQuantity_setsQuantityToZero() {
        inventory.setQuantity(50);

        inventory.deduct(50);

        assertEquals(0, inventory.getQuantity());
    }

    @Test
    void deduct_withSmallAmount_decreasesQuantity() {
        int initialQuantity = inventory.getQuantity();

        inventory.deduct(1);

        assertEquals(initialQuantity - 1, inventory.getQuantity());
    }

    @Test
    void deduct_withZeroAmount_throwsInvalidOrderStateException() {
        assertThrows(InvalidOrderStateException.class, () -> inventory.deduct(0));
        assertEquals(100, inventory.getQuantity());
    }

    @Test
    void deduct_withNegativeAmount_throwsInvalidOrderStateException() {
        assertThrows(InvalidOrderStateException.class, () -> inventory.deduct(-5));
        assertEquals(100, inventory.getQuantity());
    }

    @Test
    void deduct_withMoreThanAvailableQuantity_throwsInsufficientStockException() {
        inventory.setQuantity(30);

        assertThrows(InsufficientStockException.class, () -> inventory.deduct(50));
        assertEquals(30, inventory.getQuantity());
    }

    @Test
    void deduct_withMoreThanAvailableQuantityByOne_throwsInsufficientStockException() {
        inventory.setQuantity(10);

        assertThrows(InsufficientStockException.class, () -> inventory.deduct(11));
        assertEquals(10, inventory.getQuantity());
    }

    @Test
    void deduct_withZeroInventory_throwsInsufficientStockException() {
        inventory.setQuantity(0);

        assertThrows(InsufficientStockException.class, () -> inventory.deduct(1));
        assertEquals(0, inventory.getQuantity());
    }

    @Test
    void deduct_multipleTimes_cumulativelyDecreases() {
        inventory.deduct(20);
        assertEquals(80, inventory.getQuantity());

        inventory.deduct(30);
        assertEquals(50, inventory.getQuantity());

        inventory.deduct(50);
        assertEquals(0, inventory.getQuantity());
    }

    @Test
    void deduct_afterMultipleDeductions_throwsWhenInsufficientStock() {
        inventory.deduct(90);
        assertEquals(10, inventory.getQuantity());

        assertThrows(InsufficientStockException.class, () -> inventory.deduct(20));
        assertEquals(10, inventory.getQuantity());
    }

    @Test
    void deduct_doesNotChangeProductId() {
        UUID originalProductId = inventory.getProductId();

        inventory.deduct(10);

        assertEquals(originalProductId, inventory.getProductId());
    }
}