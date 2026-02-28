package org.brightinhere.xyz_retail.application.mapper;

import lombok.RequiredArgsConstructor;
import org.brightinhere.xyz_retail.application.dto.ProductResponse;
import org.brightinhere.xyz_retail.domain.Product;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductMapper {

    private final InventoryMapper inventoryMapper;

    public ProductResponse toResponse(Product product) {
        if (product == null) return null;

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                inventoryMapper.toResponse(product.getInventory())
        );
    }
}