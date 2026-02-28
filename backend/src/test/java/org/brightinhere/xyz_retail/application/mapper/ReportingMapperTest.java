package org.brightinhere.xyz_retail.application.mapper;

import org.brightinhere.xyz_retail.application.dto.ProductSalesResponse;
import org.brightinhere.xyz_retail.application.dto.SalesPerDayResponse;
import org.brightinhere.xyz_retail.application.port.ReportingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ReportingMapperTest {

    private ReportingMapper reportingMapper;

    @BeforeEach
    void setUp() {
        reportingMapper = new ReportingMapper();
    }

    @Test
    void toResponse_withProductSalesRow_lowStockTrue_shouldMapSuccessfully() {
        ReportingRepository.ProductSalesRow row = mock(ReportingRepository.ProductSalesRow.class);
        when(row.getProductName()).thenReturn("Test Product");
        when(row.getPrice()).thenReturn(new BigDecimal("29.99"));
        when(row.getQuantityAvailable()).thenReturn(5);
        when(row.getQuantitySold()).thenReturn(100L);

        ProductSalesResponse response = reportingMapper.toResponse(row);

        assertNotNull(response);
        assertEquals("Test Product", response.name());
        assertEquals(new BigDecimal("29.99"), response.price());
        assertEquals(5, response.quantityAvailable());
        assertTrue(response.lowStock());
        assertEquals(100, response.quantitySold());
    }

    @Test
    void toResponse_withProductSalesRow_lowStockFalse_shouldMapSuccessfully() {
        ReportingRepository.ProductSalesRow row = mock(ReportingRepository.ProductSalesRow.class);
        when(row.getProductName()).thenReturn("Another Product");
        when(row.getPrice()).thenReturn(new BigDecimal("49.99"));
        when(row.getQuantityAvailable()).thenReturn(50);
        when(row.getQuantitySold()).thenReturn(200L);

        ProductSalesResponse response = reportingMapper.toResponse(row);

        assertNotNull(response);
        assertEquals("Another Product", response.name());
        assertEquals(new BigDecimal("49.99"), response.price());
        assertEquals(50, response.quantityAvailable());
        assertFalse(response.lowStock());
        assertEquals(200, response.quantitySold());
    }

    @Test
    void toResponse_withProductSalesRow_atThresholdQuantity_shouldMarkLowStock() {
        ReportingRepository.ProductSalesRow row = mock(ReportingRepository.ProductSalesRow.class);
        when(row.getProductName()).thenReturn("Threshold Product");
        when(row.getPrice()).thenReturn(new BigDecimal("19.99"));
        when(row.getQuantityAvailable()).thenReturn(9);
        when(row.getQuantitySold()).thenReturn(50L);

        ProductSalesResponse response = reportingMapper.toResponse(row);

        assertNotNull(response);
        assertTrue(response.lowStock());
    }

    @Test
    void toResponse_withProductSalesRow_justAboveThreshold_shouldNotMarkLowStock() {
        ReportingRepository.ProductSalesRow row = mock(ReportingRepository.ProductSalesRow.class);
        when(row.getProductName()).thenReturn("Above Threshold Product");
        when(row.getPrice()).thenReturn(new BigDecimal("19.99"));
        when(row.getQuantityAvailable()).thenReturn(10);
        when(row.getQuantitySold()).thenReturn(50L);

        ProductSalesResponse response = reportingMapper.toResponse(row);

        assertNotNull(response);
        assertFalse(response.lowStock());
    }

    @Test
    void toResponse_withProductSalesRow_zeroQuantity_shouldMarkLowStock() {
        ReportingRepository.ProductSalesRow row = mock(ReportingRepository.ProductSalesRow.class);
        when(row.getProductName()).thenReturn("Out of Stock");
        when(row.getPrice()).thenReturn(new BigDecimal("9.99"));
        when(row.getQuantityAvailable()).thenReturn(0);
        when(row.getQuantitySold()).thenReturn(150L);

        ProductSalesResponse response = reportingMapper.toResponse(row);

        assertNotNull(response);
        assertTrue(response.lowStock());
        assertEquals(0, response.quantityAvailable());
    }

    @Test
    void toResponse_withSalesPerDayRow_shouldMapSuccessfully() {
        ReportingRepository.SalesPerDayRow row = mock(ReportingRepository.SalesPerDayRow.class);
        LocalDate testDate = LocalDate.of(2024, 1, 15);
        when(row.getDay()).thenReturn(testDate);
        when(row.getTotalSales()).thenReturn(new BigDecimal("1500.00"));

        SalesPerDayResponse response = reportingMapper.toResponse(row);

        assertNotNull(response);
        assertEquals(testDate, response.day());
        assertEquals(new BigDecimal("1500.00"), response.totalSales());
    }

    @Test
    void toResponse_withSalesPerDayRow_zeroSales_shouldMapSuccessfully() {
        ReportingRepository.SalesPerDayRow row = mock(ReportingRepository.SalesPerDayRow.class);
        LocalDate testDate = LocalDate.of(2024, 1, 16);
        when(row.getDay()).thenReturn(testDate);
        when(row.getTotalSales()).thenReturn(new BigDecimal("0.00"));

        SalesPerDayResponse response = reportingMapper.toResponse(row);

        assertNotNull(response);
        assertEquals(testDate, response.day());
        assertEquals(new BigDecimal("0.00"), response.totalSales());
    }

    @Test
    void toResponse_withSalesPerDayRow_highSales_shouldMapSuccessfully() {
        ReportingRepository.SalesPerDayRow row = mock(ReportingRepository.SalesPerDayRow.class);
        LocalDate testDate = LocalDate.of(2024, 1, 20);
        when(row.getDay()).thenReturn(testDate);
        when(row.getTotalSales()).thenReturn(new BigDecimal("9999.99"));

        SalesPerDayResponse response = reportingMapper.toResponse(row);

        assertNotNull(response);
        assertEquals(testDate, response.day());
        assertEquals(new BigDecimal("9999.99"), response.totalSales());
    }
}