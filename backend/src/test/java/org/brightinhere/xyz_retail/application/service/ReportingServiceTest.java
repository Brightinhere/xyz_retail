package org.brightinhere.xyz_retail.application.service;

    import org.brightinhere.xyz_retail.application.port.ReportingRepository;
    import org.brightinhere.xyz_retail.infrastructure.exception.InvalidOrderStateException;
    import org.junit.jupiter.api.BeforeEach;
    import org.junit.jupiter.api.Test;
    import org.junit.jupiter.api.extension.ExtendWith;
    import org.mockito.InjectMocks;
    import org.mockito.Mock;
    import org.mockito.junit.jupiter.MockitoExtension;

    import java.math.BigDecimal;
    import java.time.LocalDate;
    import java.time.YearMonth;
    import java.util.List;

    import static org.junit.jupiter.api.Assertions.*;
    import static org.mockito.ArgumentMatchers.any;
    import static org.mockito.ArgumentMatchers.eq;
    import static org.mockito.Mockito.*;

    @ExtendWith(MockitoExtension.class)
    class ReportingServiceTest {

        @Mock
        private ReportingRepository reportingRepository;

        @InjectMocks
        private ReportingService reportingService;

        private List<ReportingRepository.ProductSalesRow> mockProductSalesRows;
        private List<ReportingRepository.SalesPerDayRow> mockSalesPerDayRows;

        @BeforeEach
        void setUp() {
            mockProductSalesRows = List.of(
                    createProductSalesRow("Product A", 100),
                    createProductSalesRow("Product B", 85),
                    createProductSalesRow("Product C", 70)
            );

            mockSalesPerDayRows = List.of(
                    createSalesPerDayRow(LocalDate.of(2024, 1, 1), 1500.00),
                    createSalesPerDayRow(LocalDate.of(2024, 1, 2), 2000.00),
                    createSalesPerDayRow(LocalDate.of(2024, 1, 3), 1800.00)
            );
        }

        @Test
        void top5SellingProductsOfDay_withValidDay_returnsTopSellingProducts() {
            LocalDate testDay = LocalDate.of(2024, 1, 15);
            when(reportingRepository.topSellingProductsOfDay(testDay, 5))
                    .thenReturn(mockProductSalesRows);

            List<ReportingRepository.ProductSalesRow> result = reportingService.top5SellingProductsOfDay(testDay);

            assertNotNull(result);
            assertEquals(3, result.size());
            verify(reportingRepository, times(1)).topSellingProductsOfDay(testDay, 5);
        }

        @Test
        void top5SellingProductsOfDay_withNullDay_usesCurrentDate() {
            LocalDate today = LocalDate.now();
            when(reportingRepository.topSellingProductsOfDay(today, 5))
                    .thenReturn(mockProductSalesRows);

            List<ReportingRepository.ProductSalesRow> result = reportingService.top5SellingProductsOfDay(null);

            assertNotNull(result);
            assertEquals(3, result.size());
            verify(reportingRepository, times(1)).topSellingProductsOfDay(today, 5);
        }

        @Test
        void top5SellingProductsOfDay_withEmptyResult_returnsEmptyList() {
            LocalDate testDay = LocalDate.of(2024, 1, 15);
            when(reportingRepository.topSellingProductsOfDay(testDay, 5))
                    .thenReturn(List.of());

            List<ReportingRepository.ProductSalesRow> result = reportingService.top5SellingProductsOfDay(testDay);

            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(reportingRepository, times(1)).topSellingProductsOfDay(testDay, 5);
        }

        @Test
        void leastSellingProductsOfMonth_withValidMonth_returnsLeastSellingProducts() {
            YearMonth testMonth = YearMonth.of(2024, 1);
            when(reportingRepository.leastSellingProductsOfMonth(testMonth, 5))
                    .thenReturn(mockProductSalesRows);

            List<ReportingRepository.ProductSalesRow> result = reportingService.leastSellingProductsOfMonth(testMonth);

            assertNotNull(result);
            assertEquals(3, result.size());
            verify(reportingRepository, times(1)).leastSellingProductsOfMonth(testMonth, 5);
        }

        @Test
        void leastSellingProductsOfMonth_withNullMonth_usesCurrentMonth() {
            YearMonth today = YearMonth.now();
            when(reportingRepository.leastSellingProductsOfMonth(today, 5))
                    .thenReturn(mockProductSalesRows);

            List<ReportingRepository.ProductSalesRow> result = reportingService.leastSellingProductsOfMonth(null);

            assertNotNull(result);
            assertEquals(3, result.size());
            verify(reportingRepository, times(1)).leastSellingProductsOfMonth(today, 5);
        }

        @Test
        void leastSellingProductsOfMonth_withEmptyResult_returnsEmptyList() {
            YearMonth testMonth = YearMonth.of(2024, 1);
            when(reportingRepository.leastSellingProductsOfMonth(testMonth, 5))
                    .thenReturn(List.of());

            List<ReportingRepository.ProductSalesRow> result = reportingService.leastSellingProductsOfMonth(testMonth);

            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(reportingRepository, times(1)).leastSellingProductsOfMonth(testMonth, 5);
        }

        @Test
        void saleAmountPerDay_withValidDateRange_returnsSalesData() {
            LocalDate startDate = LocalDate.of(2024, 1, 1);
            LocalDate endDate = LocalDate.of(2024, 1, 3);
            when(reportingRepository.saleAmountPerDay(startDate, endDate))
                    .thenReturn(mockSalesPerDayRows);

            List<ReportingRepository.SalesPerDayRow> result = reportingService.saleAmountPerDay(startDate, endDate);

            assertNotNull(result);
            assertEquals(3, result.size());
            verify(reportingRepository, times(1)).saleAmountPerDay(startDate, endDate);
        }

        @Test
        void saleAmountPerDay_withSameDateRange_returnsSalesData() {
            LocalDate date = LocalDate.of(2024, 1, 1);
            when(reportingRepository.saleAmountPerDay(date, date))
                    .thenReturn(List.of(mockSalesPerDayRows.get(0)));

            List<ReportingRepository.SalesPerDayRow> result = reportingService.saleAmountPerDay(date, date);

            assertNotNull(result);
            assertEquals(1, result.size());
            verify(reportingRepository, times(1)).saleAmountPerDay(date, date);
        }

        @Test
        void saleAmountPerDay_withEmptyResult_returnsEmptyList() {
            LocalDate startDate = LocalDate.of(2024, 1, 1);
            LocalDate endDate = LocalDate.of(2024, 1, 3);
            when(reportingRepository.saleAmountPerDay(startDate, endDate))
                    .thenReturn(List.of());

            List<ReportingRepository.SalesPerDayRow> result = reportingService.saleAmountPerDay(startDate, endDate);

            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(reportingRepository, times(1)).saleAmountPerDay(startDate, endDate);
        }

        @Test
        void saleAmountPerDay_withNullStartDate_throwsException() {
            LocalDate endDate = LocalDate.of(2024, 1, 3);

            assertThrows(InvalidOrderStateException.class,
                    () -> reportingService.saleAmountPerDay(null, endDate));

            verify(reportingRepository, never()).saleAmountPerDay(any(), any());
        }

        @Test
        void saleAmountPerDay_withNullEndDate_throwsException() {
            LocalDate startDate = LocalDate.of(2024, 1, 1);

            assertThrows(InvalidOrderStateException.class,
                    () -> reportingService.saleAmountPerDay(startDate, null));

            verify(reportingRepository, never()).saleAmountPerDay(any(), any());
        }

        @Test
        void saleAmountPerDay_withBothNullDates_throwsException() {
            assertThrows(InvalidOrderStateException.class,
                    () -> reportingService.saleAmountPerDay(null, null));

            verify(reportingRepository, never()).saleAmountPerDay(any(), any());
        }

        @Test
        void saleAmountPerDay_whenEndDateIsBeforeStartDate_throwsException() {
            LocalDate startDate = LocalDate.of(2024, 1, 10);
            LocalDate endDate = LocalDate.of(2024, 1, 5);

            assertThrows(InvalidOrderStateException.class,
                    () -> reportingService.saleAmountPerDay(startDate, endDate));

            verify(reportingRepository, never()).saleAmountPerDay(any(), any());
        }

        private ReportingRepository.ProductSalesRow createProductSalesRow(String productName, long quantity) {
            return mock(ReportingRepository.ProductSalesRow.class);
        }

        private ReportingRepository.SalesPerDayRow createSalesPerDayRow(LocalDate date, double amount) {
            return mock(ReportingRepository.SalesPerDayRow.class);
        }
    }