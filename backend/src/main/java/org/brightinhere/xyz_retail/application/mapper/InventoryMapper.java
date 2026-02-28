package org.brightinhere.xyz_retail.application.mapper;

import org.brightinhere.xyz_retail.application.dto.InventoryResponse;
import org.brightinhere.xyz_retail.domain.Inventory;
import org.springframework.stereotype.Component;

@Component
public class InventoryMapper {

    public InventoryResponse toResponse(Inventory inventory) {
        if (inventory == null) return null;

        int qty = inventory.getQuantity();
        boolean lowStock = qty < 10;

        return new InventoryResponse(qty, lowStock);
    }
}