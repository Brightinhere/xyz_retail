package org.brightinhere.xyz_retail.infrastructure.web;

import org.brightinhere.xyz_retail.application.dto.InventoryResponse;
import org.brightinhere.xyz_retail.application.dto.ProductResponse;
import org.brightinhere.xyz_retail.application.mapper.ProductMapper;
import org.brightinhere.xyz_retail.application.service.ProductService;
import org.brightinhere.xyz_retail.domain.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private ProductMapper productMapper;

    @Test
    @DisplayName("Should return matching products for a valid query")
    void shouldReturnMatchingProducts() throws Exception {
        UUID productId = UUID.randomUUID();
        Product product = new Product();
        product.setId(productId);

        InventoryResponse inventoryResponse = new InventoryResponse(50, false);
        ProductResponse response = new ProductResponse(productId, "Apple", "A juicy Apple", new BigDecimal("999.99"), inventoryResponse);

        when(productService.search("Apple")).thenReturn(List.of(product));
        when(productMapper.toResponse(product)).thenReturn(response);

        mockMvc.perform(get("/api/products/search")
                        .param("q", "Apple")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(productId.toString()))
                .andExpect(jsonPath("$[0].name").value("Apple"))
                .andExpect(jsonPath("$[0].description").value("A juicy Apple"))
                .andExpect(jsonPath("$[0].price").value(999.99))
                .andExpect(jsonPath("$[0].inventory.quantity").value(50))
                .andExpect(jsonPath("$[0].inventory.lowStock").value(false));

        verify(productService).search("Apple");
        verify(productMapper).toResponse(product);
    }

    @Test
    @DisplayName("Should return multiple products")
    void shouldReturnMultipleProducts() throws Exception {
        Product product1 = new Product();
        product1.setId(UUID.randomUUID());
        Product product2 = new Product();
        product2.setId(UUID.randomUUID());

        ProductResponse resp1 = new ProductResponse(product1.getId(), "Apple", "Desc 1", new BigDecimal("999.99"), new InventoryResponse(50, false));
        ProductResponse resp2 = new ProductResponse(product2.getId(), "Apple Bag", "Desc 2", new BigDecimal("49.99"), new InventoryResponse(5, true));

        when(productService.search("Apple")).thenReturn(List.of(product1, product2));
        when(productMapper.toResponse(product1)).thenReturn(resp1);
        when(productMapper.toResponse(product2)).thenReturn(resp2);

        mockMvc.perform(get("/api/products/search")
                        .param("q", "Apple")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Apple"))
                .andExpect(jsonPath("$[1].name").value("Apple Bag"))
                .andExpect(jsonPath("$[1].inventory.lowStock").value(true));

        verify(productService).search("Apple");
        verify(productMapper).toResponse(product1);
        verify(productMapper).toResponse(product2);
    }

    @Test
    @DisplayName("Should return empty list when no products match")
    void shouldReturnEmptyListWhenNoMatch() throws Exception {
        when(productService.search("NonExistent")).thenReturn(List.of());

        mockMvc.perform(get("/api/products/search")
                        .param("q", "NonExistent")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(productService).search("NonExistent");
        verify(productMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("Should return empty list when query is empty string")
    void shouldReturnEmptyListWhenQueryEmpty() throws Exception {
        when(productService.search("")).thenReturn(List.of());

        mockMvc.perform(get("/api/products/search")
                        .param("q", "")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(productService).search("");
        verify(productMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("Should return 400 when q parameter is missing")
    void shouldReturn400WhenQueryParamMissing() throws Exception {
        mockMvc.perform(get("/api/products/search")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(productService, never()).search(any());
    }

    @Test
    @DisplayName("Should return JSON content type")
    void shouldReturnJsonContentType() throws Exception {
        when(productService.search("test")).thenReturn(List.of());

        mockMvc.perform(get("/api/products/search")
                        .param("q", "test")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Should pass query parameter directly to service")
    void shouldPassQueryToService() throws Exception {
        String query = "  Apple  ";
        when(productService.search(query)).thenReturn(List.of());

        mockMvc.perform(get("/api/products/search")
                        .param("q", query)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(productService).search(query);
    }

    @Test
    @DisplayName("Should return product with null inventory")
    void shouldReturnProductWithNullInventory() throws Exception {
        UUID productId = UUID.randomUUID();
        Product product = new Product();
        product.setId(productId);

        ProductResponse response = new ProductResponse(productId, "Widget", "A widget", new BigDecimal("9.99"), null);

        when(productService.search("Widget")).thenReturn(List.of(product));
        when(productMapper.toResponse(product)).thenReturn(response);

        mockMvc.perform(get("/api/products/search")
                        .param("q", "Widget")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Widget"))
                .andExpect(jsonPath("$[0].inventory").isEmpty());
    }
}