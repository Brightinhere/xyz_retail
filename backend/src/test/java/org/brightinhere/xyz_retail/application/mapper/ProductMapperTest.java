package org.brightinhere.xyz_retail.application.mapper;

import org.brightinhere.xyz_retail.application.dto.InventoryResponse;
import org.brightinhere.xyz_retail.application.dto.ProductResponse;
import org.brightinhere.xyz_retail.domain.Inventory;
import org.brightinhere.xyz_retail.domain.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductMapperTest {

    @Mock
    private InventoryMapper inventoryMapper;

    @InjectMocks
    private ProductMapper productMapper;

    private Product product;
    private Inventory inventory;
    private UUID productId;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();

        inventory = new Inventory();
        inventory.setProductId(productId);
        inventory.setQuantity(100);

        product = new Product();
        product.setId(productId);
        product.setName("Test Product");
        product.setDescription("A test product description");
        product.setPrice(new BigDecimal("29.99"));
        product.setInventory(inventory);
    }

    @Test
    void toResponse_withValidProduct_shouldMapSuccessfully() {
        InventoryResponse inventoryResponse = new InventoryResponse(100, false);

        when(inventoryMapper.toResponse(inventory)).thenReturn(inventoryResponse);

        ProductResponse response = productMapper.toResponse(product);

        assertNotNull(response);
        assertEquals(productId, response.id());
        assertEquals("Test Product", response.name());
        assertEquals("A test product description", response.description());
        assertEquals(new BigDecimal("29.99"), response.price());
        assertEquals(inventoryResponse, response.inventory());
    }

    @Test
    void toResponse_withNullProduct_shouldReturnNull() {
        ProductResponse response = productMapper.toResponse(null);

        assertNull(response);
    }

    @Test
    void toResponse_withNullInventory_shouldMapProductWithNullInventory() {
        product.setInventory(null);

        when(inventoryMapper.toResponse(null)).thenReturn(null);

        ProductResponse response = productMapper.toResponse(product);

        assertNotNull(response);
        assertEquals(productId, response.id());
        assertEquals("Test Product", response.name());
        assertNull(response.inventory());
    }

    @Test
    void toResponse_withEmptyDescription_shouldMapSuccessfully() {
        product.setDescription("");
        InventoryResponse inventoryResponse = new InventoryResponse(100, false);

        when(inventoryMapper.toResponse(inventory)).thenReturn(inventoryResponse);

        ProductResponse response = productMapper.toResponse(product);

        assertNotNull(response);
        assertEquals("", response.description());
    }

    @Test
    void toResponse_withZeroPrice_shouldMapSuccessfully() {
        product.setPrice(new BigDecimal("0.00"));
        InventoryResponse inventoryResponse = new InventoryResponse(100, false);

        when(inventoryMapper.toResponse(inventory)).thenReturn(inventoryResponse);

        ProductResponse response = productMapper.toResponse(product);

        assertNotNull(response);
        assertEquals(new BigDecimal("0.00"), response.price());
    }

    @Test
    void toResponse_withHighPrice_shouldMapSuccessfully() {
        product.setPrice(new BigDecimal("9999.99"));
        InventoryResponse inventoryResponse = new InventoryResponse(100, false);

        when(inventoryMapper.toResponse(inventory)).thenReturn(inventoryResponse);

        ProductResponse response = productMapper.toResponse(product);

        assertNotNull(response);
        assertEquals(new BigDecimal("9999.99"), response.price());
    }
}