package org.brightinhere.xyz_retail.application.service;

import org.brightinhere.xyz_retail.application.port.ProductRepository;
import org.brightinhere.xyz_retail.domain.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        product1 = new Product();
        product1.setId(UUID.randomUUID());
        product1.setName("Laptop");
        product1.setPrice(new BigDecimal("999.99"));

        product2 = new Product();
        product2.setId(UUID.randomUUID());
        product2.setName("Laptop Bag");
        product2.setPrice(new BigDecimal("49.99"));
    }

    @Test
    void search_withValidQuery_returnsMatchingProducts() {
        String query = "Laptop";
        List<Product> expectedProducts = List.of(product1, product2);
        when(productRepository.search("Laptop")).thenReturn(expectedProducts);

        List<Product> result = productService.search(query);

        assertEquals(2, result.size());
        assertEquals(product1, result.get(0));
        assertEquals(product2, result.get(1));
        verify(productRepository, times(1)).search("Laptop");
    }

    @Test
    void search_withNoMatches_returnsEmptyList() {
        String query = "NonExistentProduct";
        when(productRepository.search("NonExistentProduct")).thenReturn(List.of());

        List<Product> result = productService.search(query);

        assertTrue(result.isEmpty());
        verify(productRepository, times(1)).search("NonExistentProduct");
    }

    @Test
    void search_withNullQuery_returnsEmptyList() {
        List<Product> result = productService.search(null);

        assertTrue(result.isEmpty());
        verify(productRepository, never()).search(anyString());
    }

    @Test
    void search_withEmptyQuery_returnsEmptyList() {
        List<Product> result = productService.search("");

        assertTrue(result.isEmpty());
        verify(productRepository, never()).search(anyString());
    }

    @Test
    void search_withWhitespaceQuery_returnsEmptyList() {
        List<Product> result = productService.search("   ");

        assertTrue(result.isEmpty());
        verify(productRepository, never()).search(anyString());
    }

    @Test
    void search_trimmesQueryWhitespace() {
        String query = "  Laptop  ";
        List<Product> expectedProducts = List.of(product1);
        when(productRepository.search("Laptop")).thenReturn(expectedProducts);

        List<Product> result = productService.search(query);

        assertEquals(1, result.size());
        verify(productRepository, times(1)).search("Laptop");
    }

    @Test
    void search_withSingleResult_returnsProduct() {
        String query = "Monitor";
        List<Product> expectedProducts = List.of(product1);
        when(productRepository.search("Monitor")).thenReturn(expectedProducts);

        List<Product> result = productService.search(query);

        assertEquals(1, result.size());
        assertEquals(product1, result.get(0));
        verify(productRepository, times(1)).search("Monitor");
    }
}