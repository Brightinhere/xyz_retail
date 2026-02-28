package org.brightinhere.xyz_retail.application.mapper;

import org.brightinhere.xyz_retail.application.dto.InventoryResponse;
import org.brightinhere.xyz_retail.domain.Inventory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InventoryMapperTest {

    private final InventoryMapper mapper = new InventoryMapper();

    @Test
    void toResponse_whenInventoryIsNull_returnsNull() {
        assertNull(mapper.toResponse(null));
    }

    @Test
    void toResponse_whenQtyBelow10_lowStockIsTrue() {
        Inventory inv = new Inventory();
        inv.setQuantity(9);

        InventoryResponse response = mapper.toResponse(inv);

        assertNotNull(response);
        assertEquals(9, response.quantity());
        assertTrue(response.lowStock());
    }

    @Test
    void toResponse_whenQtyIs10OrMore_lowStockIsFalse() {
        Inventory inv = new Inventory();
        inv.setQuantity(10);

        InventoryResponse response = mapper.toResponse(inv);

        assertNotNull(response);
        assertEquals(10, response.quantity());
        assertFalse(response.lowStock());
    }

    @Test
    void toResponse_whenQtyIsNegative_lowStockIsTrue() {
        Inventory inv = new Inventory();
        inv.setQuantity(-5);

        InventoryResponse response = mapper.toResponse(inv);

        assertNotNull(response);
        assertEquals(-5, response.quantity());
        assertTrue(response.lowStock());
    }
}