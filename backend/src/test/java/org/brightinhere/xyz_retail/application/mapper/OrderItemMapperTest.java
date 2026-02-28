package org.brightinhere.xyz_retail.application.mapper;

import org.brightinhere.xyz_retail.application.dto.OrderItemResponse;
import org.brightinhere.xyz_retail.domain.OrderItem;
import org.brightinhere.xyz_retail.domain.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderItemMapperTest {

    private OrderItemMapper mapper;
    private Product product;

    @BeforeEach
    void setUp() {
        mapper = new OrderItemMapper();
        product = new Product();
        product.setId(UUID.randomUUID());
        product.setName("Test Product");
    }

    @Test
    void toResponse_whenOrderItemIsNull_returnsNull() {
        assertNull(mapper.toResponse(null));
    }

    @Test
    void toResponse_whenOrderItemIsValid_mapsAllFields() {
        UUID productId = UUID.randomUUID();
        product.setId(productId);
        product.setName("Lemon");

        OrderItem item = new OrderItem();
        item.setProduct(product);
        item.setQuantity(2);
        item.setSalePrice(new BigDecimal("999.99"));

        OrderItemResponse response = mapper.toResponse(item);

        assertNotNull(response);
        assertEquals(productId, response.productId());
        assertEquals("Lemon", response.productName());
        assertEquals(2, response.quantity());
        assertEquals(new BigDecimal("999.99"), response.salePrice());
    }

    @Test
    void toResponse_whenQuantityIsZero_mapsCorrectly() {
        OrderItem item = new OrderItem();
        item.setProduct(product);
        item.setQuantity(0);
        item.setSalePrice(new BigDecimal("25.00"));

        OrderItemResponse response = mapper.toResponse(item);

        assertNotNull(response);
        assertEquals(0, response.quantity());
    }

    @Test
    void toResponse_whenSalePriceIsZero_mapsCorrectly() {
        OrderItem item = new OrderItem();
        item.setProduct(product);
        item.setQuantity(1);
        item.setSalePrice(BigDecimal.ZERO);

        OrderItemResponse response = mapper.toResponse(item);

        assertNotNull(response);
        assertEquals(BigDecimal.ZERO, response.salePrice());
    }

    @Test
    void toResponse_whenProductHasNullId_mapsCorrectly() {
        product.setId(null);
        product.setName("New Product");

        OrderItem item = new OrderItem();
        item.setProduct(product);
        item.setQuantity(1);
        item.setSalePrice(new BigDecimal("50.00"));

        OrderItemResponse response = mapper.toResponse(item);

        assertNotNull(response);
        assertNull(response.productId());
    }
}