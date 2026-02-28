package org.brightinhere.xyz_retail.infrastructure.persistence.repositories;

import org.brightinhere.xyz_retail.application.port.ReportingRepository.ProductSalesRow;
import org.brightinhere.xyz_retail.application.port.ReportingRepository.SalesPerDayRow;
import org.brightinhere.xyz_retail.infrastructure.persistence.jpa.ReportingSpringDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportingRepositoryImplTest {

    @Mock
    private ReportingSpringDataRepository jpa;

    private ReportingRepositoryImpl reportingRepository;

    @BeforeEach
    void setUp() {
        reportingRepository = new ReportingRepositoryImpl(jpa);
    }

    @Test
    @DisplayName("Should return top selling products of day with specified limit")
    void shouldReturnTopSellingProductsOfDay() {
        LocalDate day = LocalDate.of(2025, 1, 15);
        int limit = 10;
        List<ProductSalesRow> expected = createProductSalesRows(3);

        when(jpa.topSellingProductsOfDay(eq(day), any(Pageable.class)))
                .thenReturn(expected);

        List<ProductSalesRow> result = reportingRepository.topSellingProductsOfDay(day, limit);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(expected, result);
        verify(jpa, times(1)).topSellingProductsOfDay(eq(day), eq(PageRequest.of(0, limit)));
    }

    @Test
    @DisplayName("Should return empty list when no products sold on given day")
    void shouldReturnEmptyListWhenNoProductsSoldOnDay() {
        LocalDate day = LocalDate.of(2025, 1, 15);
        int limit = 10;

        when(jpa.topSellingProductsOfDay(eq(day), any(Pageable.class)))
                .thenReturn(new ArrayList<>());

        List<ProductSalesRow> result = reportingRepository.topSellingProductsOfDay(day, limit);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(jpa, times(1)).topSellingProductsOfDay(eq(day), any(Pageable.class));
    }

    @Test
    @DisplayName("Should use correct pagination limit for top selling products")
    void shouldUseCorrectPaginationLimitForTopSellingProducts() {
        LocalDate day = LocalDate.of(2025, 1, 15);
        int limit = 5;
        List<ProductSalesRow> expected = createProductSalesRows(5);

        when(jpa.topSellingProductsOfDay(eq(day), any(Pageable.class)))
                .thenReturn(expected);

        reportingRepository.topSellingProductsOfDay(day, limit);

        verify(jpa).topSellingProductsOfDay(eq(day), eq(PageRequest.of(0, limit)));
    }

    @Test
    @DisplayName("Should use first page (0) for top selling products pagination")
    void shouldUseFirstPageForTopSellingProducts() {
        LocalDate day = LocalDate.of(2025, 1, 15);
        int limit = 10;

        when(jpa.topSellingProductsOfDay(eq(day), any(Pageable.class)))
                .thenReturn(new ArrayList<>());

        reportingRepository.topSellingProductsOfDay(day, limit);

        verify(jpa).topSellingProductsOfDay(eq(day), eq(PageRequest.of(0, limit)));
    }

    @Test
    @DisplayName("Should return least selling products of specified month")
    void shouldReturnLeastSellingProductsOfMonth() {
        YearMonth month = YearMonth.of(2025, 1);
        int limit = 10;
        List<ProductSalesRow> expected = createProductSalesRows(2);

        when(jpa.leastSellingProductsOfMonth(any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(expected);

        List<ProductSalesRow> result = reportingRepository.leastSellingProductsOfMonth(month, limit);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expected, result);
        verify(jpa, times(1)).leastSellingProductsOfMonth(any(LocalDateTime.class), any(LocalDateTime.class), eq(PageRequest.of(0, limit)));
    }

    @Test
    @DisplayName("Should use current month when month parameter is null")
    void shouldUseCurrentMonthWhenMonthIsNull() {
        YearMonth currentMonth = YearMonth.now();
        int limit = 10;
        List<ProductSalesRow> expected = createProductSalesRows(1);

        when(jpa.leastSellingProductsOfMonth(any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(expected);

        reportingRepository.leastSellingProductsOfMonth(null, limit);

        verify(jpa).leastSellingProductsOfMonth(
                eq(currentMonth.atDay(1).atStartOfDay()),
                eq(currentMonth.plusMonths(1).atDay(1).atStartOfDay()),
                eq(PageRequest.of(0, limit))
        );
    }

    @Test
    @DisplayName("Should calculate correct start date for month range")
    void shouldCalculateCorrectStartDateForMonthRange() {
        YearMonth month = YearMonth.of(2025, 3);
        int limit = 5;
        LocalDateTime expectedStart = LocalDateTime.of(2025, 3, 1, 0, 0, 0);

        when(jpa.leastSellingProductsOfMonth(any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new ArrayList<>());

        reportingRepository.leastSellingProductsOfMonth(month, limit);

        verify(jpa).leastSellingProductsOfMonth(
                eq(expectedStart),
                any(LocalDateTime.class),
                any(Pageable.class)
        );
    }

    @Test
    @DisplayName("Should calculate correct end date for month range")
    void shouldCalculateCorrectEndDateForMonthRange() {
        YearMonth month = YearMonth.of(2025, 3);
        int limit = 5;
        LocalDateTime expectedEnd = LocalDateTime.of(2025, 4, 1, 0, 0, 0);

        when(jpa.leastSellingProductsOfMonth(any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new ArrayList<>());

        reportingRepository.leastSellingProductsOfMonth(month, limit);

        verify(jpa).leastSellingProductsOfMonth(
                any(LocalDateTime.class),
                eq(expectedEnd),
                any(Pageable.class)
        );
    }

    @Test
    @DisplayName("Should return empty list when no products sold in month")
    void shouldReturnEmptyListWhenNoProductsSoldInMonth() {
        YearMonth month = YearMonth.of(2025, 1);
        int limit = 10;

        when(jpa.leastSellingProductsOfMonth(any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new ArrayList<>());

        List<ProductSalesRow> result = reportingRepository.leastSellingProductsOfMonth(month, limit);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should use correct pagination limit for least selling products")
    void shouldUseCorrectPaginationLimitForLeastSellingProducts() {
        YearMonth month = YearMonth.of(2025, 1);
        int limit = 7;
        List<ProductSalesRow> expected = createProductSalesRows(7);

        when(jpa.leastSellingProductsOfMonth(any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(expected);

        reportingRepository.leastSellingProductsOfMonth(month, limit);

        verify(jpa).leastSellingProductsOfMonth(any(LocalDateTime.class), any(LocalDateTime.class), eq(PageRequest.of(0, limit)));
    }

    @Test
    @DisplayName("Should return sales amount per day for date range")
    void shouldReturnSalesAmountPerDay() {
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 1, 31);
        List<SalesPerDayRow> expected = createSalesPerDayRows(5);

        when(jpa.saleAmountPerDay(eq(start), eq(end)))
                .thenReturn(expected);

        List<SalesPerDayRow> result = reportingRepository.saleAmountPerDay(start, end);

        assertNotNull(result);
        assertEquals(5, result.size());
        assertEquals(expected, result);
        verify(jpa, times(1)).saleAmountPerDay(eq(start), eq(end));
    }

    @Test
    @DisplayName("Should return empty list when no sales in date range")
    void shouldReturnEmptyListWhenNoSalesInDateRange() {
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 1, 31);

        when(jpa.saleAmountPerDay(eq(start), eq(end)))
                .thenReturn(new ArrayList<>());

        List<SalesPerDayRow> result = reportingRepository.saleAmountPerDay(start, end);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(jpa, times(1)).saleAmountPerDay(eq(start), eq(end));
    }

    @Test
    @DisplayName("Should pass correct date range to JPA repository")
    void shouldPassCorrectDateRangeToJpaRepository() {
        LocalDate start = LocalDate.of(2025, 1, 15);
        LocalDate end = LocalDate.of(2025, 1, 20);

        when(jpa.saleAmountPerDay(eq(start), eq(end)))
                .thenReturn(new ArrayList<>());

        reportingRepository.saleAmountPerDay(start, end);

        verify(jpa).saleAmountPerDay(eq(start), eq(end));
    }

    @Test
    @DisplayName("Should handle single day date range for sales amount")
    void shouldHandleSingleDayDateRangeForSalesAmount() {
        LocalDate day = LocalDate.of(2025, 1, 15);
        List<SalesPerDayRow> expected = createSalesPerDayRows(1);

        when(jpa.saleAmountPerDay(eq(day), eq(day)))
                .thenReturn(expected);

        List<SalesPerDayRow> result = reportingRepository.saleAmountPerDay(day, day);

        assertNotNull(result);
        verify(jpa).saleAmountPerDay(eq(day), eq(day));
    }

    private List<ProductSalesRow> createProductSalesRows(int count) {
        List<ProductSalesRow> rows = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            rows.add(mock(ProductSalesRow.class));
        }
        return rows;
    }

    private List<SalesPerDayRow> createSalesPerDayRows(int count) {
        List<SalesPerDayRow> rows = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            rows.add(mock(SalesPerDayRow.class));
        }
        return rows;
    }
}