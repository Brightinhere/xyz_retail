package org.brightinhere.xyz_retail.infrastructure.web;

import org.brightinhere.xyz_retail.application.dto.ProductSalesResponse;
import org.brightinhere.xyz_retail.application.dto.SalesPerDayResponse;
import org.brightinhere.xyz_retail.application.mapper.ReportingMapper;
import org.brightinhere.xyz_retail.application.port.ReportingRepository;
import org.brightinhere.xyz_retail.application.service.ReportingService;
import org.brightinhere.xyz_retail.infrastructure.exception.InvalidOrderStateException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ManagementReportController.class)
class ManagementReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReportingService reportingService;

    @MockBean
    private ReportingMapper reportingMapper;

    @Nested
    @DisplayName("GET /api/management/reports/top-selling")
    class TopSelling {

        @Test
        @DisplayName("Should return top selling products with date parameter")
        void shouldReturnTopSellingWithDate() throws Exception {
            LocalDate date = LocalDate.of(2026, 2, 28);
            ReportingRepository.ProductSalesRow row = mock(ReportingRepository.ProductSalesRow.class);
            ProductSalesResponse response = new ProductSalesResponse("Apple", new BigDecimal("1.50"), 100, false, 50L);

            when(reportingService.top5SellingProductsOfDay(date)).thenReturn(List.of(row));
            when(reportingMapper.toResponse(row)).thenReturn(response);

            mockMvc.perform(get("/api/management/reports/top-selling")
                            .param("date", "2026-02-28")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].name").value("Apple"))
                    .andExpect(jsonPath("$[0].price").value(1.50))
                    .andExpect(jsonPath("$[0].quantityAvailable").value(100))
                    .andExpect(jsonPath("$[0].lowStock").value(false))
                    .andExpect(jsonPath("$[0].quantitySold").value(50));

            verify(reportingService).top5SellingProductsOfDay(date);
            verify(reportingMapper).toResponse(row);
        }

        @Test
        @DisplayName("Should return top selling products without date parameter (defaults to null)")
        void shouldReturnTopSellingWithoutDate() throws Exception {
            ReportingRepository.ProductSalesRow row = mock(ReportingRepository.ProductSalesRow.class);
            ProductSalesResponse response = new ProductSalesResponse("Banana", new BigDecimal("0.99"), 5, true, 200L);

            when(reportingService.top5SellingProductsOfDay(null)).thenReturn(List.of(row));
            when(reportingMapper.toResponse(row)).thenReturn(response);

            mockMvc.perform(get("/api/management/reports/top-selling")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].name").value("Banana"))
                    .andExpect(jsonPath("$[0].lowStock").value(true));

            verify(reportingService).top5SellingProductsOfDay(null);
        }

        @Test
        @DisplayName("Should return empty list when no top selling products")
        void shouldReturnEmptyList() throws Exception {
            when(reportingService.top5SellingProductsOfDay(null)).thenReturn(List.of());

            mockMvc.perform(get("/api/management/reports/top-selling")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));

            verify(reportingMapper, never()).toResponse(any(ReportingRepository.ProductSalesRow.class));
        }

        @Test
        @DisplayName("Should return multiple top selling products")
        void shouldReturnMultipleProducts() throws Exception {
            ReportingRepository.ProductSalesRow row1 = mock(ReportingRepository.ProductSalesRow.class);
            ReportingRepository.ProductSalesRow row2 = mock(ReportingRepository.ProductSalesRow.class);
            ProductSalesResponse resp1 = new ProductSalesResponse("Apple", new BigDecimal("1.50"), 100, false, 50L);
            ProductSalesResponse resp2 = new ProductSalesResponse("Orange", new BigDecimal("2.00"), 8, true, 30L);

            when(reportingService.top5SellingProductsOfDay(null)).thenReturn(List.of(row1, row2));
            when(reportingMapper.toResponse(row1)).thenReturn(resp1);
            when(reportingMapper.toResponse(row2)).thenReturn(resp2);

            mockMvc.perform(get("/api/management/reports/top-selling")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].name").value("Apple"))
                    .andExpect(jsonPath("$[1].name").value("Orange"));
        }
    }

    @Nested
    @DisplayName("GET /api/management/reports/least-selling")
    class LeastSelling {

        @Test
        @DisplayName("Should return least selling products with month parameter")
        void shouldReturnLeastSellingWithMonth() throws Exception {
            YearMonth month = YearMonth.of(2026, 2);
            ReportingRepository.ProductSalesRow row = mock(ReportingRepository.ProductSalesRow.class);
            ProductSalesResponse response = new ProductSalesResponse("Kiwi", new BigDecimal("3.00"), 200, false, 2L);

            when(reportingService.leastSellingProductsOfMonth(month)).thenReturn(List.of(row));
            when(reportingMapper.toResponse(row)).thenReturn(response);

            mockMvc.perform(get("/api/management/reports/least-selling")
                            .param("month", "2026-02")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].name").value("Kiwi"))
                    .andExpect(jsonPath("$[0].price").value(3.00))
                    .andExpect(jsonPath("$[0].quantityAvailable").value(200))
                    .andExpect(jsonPath("$[0].lowStock").value(false))
                    .andExpect(jsonPath("$[0].quantitySold").value(2));

            verify(reportingService).leastSellingProductsOfMonth(month);
            verify(reportingMapper).toResponse(row);
        }

        @Test
        @DisplayName("Should return least selling products without month parameter (defaults to null)")
        void shouldReturnLeastSellingWithoutMonth() throws Exception {
            ReportingRepository.ProductSalesRow row = mock(ReportingRepository.ProductSalesRow.class);
            ProductSalesResponse response = new ProductSalesResponse("Mango", new BigDecimal("4.50"), 3, true, 1L);

            when(reportingService.leastSellingProductsOfMonth(null)).thenReturn(List.of(row));
            when(reportingMapper.toResponse(row)).thenReturn(response);

            mockMvc.perform(get("/api/management/reports/least-selling")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].name").value("Mango"))
                    .andExpect(jsonPath("$[0].lowStock").value(true));

            verify(reportingService).leastSellingProductsOfMonth(null);
        }

        @Test
        @DisplayName("Should return empty list when no least selling products")
        void shouldReturnEmptyList() throws Exception {
            when(reportingService.leastSellingProductsOfMonth(null)).thenReturn(List.of());

            mockMvc.perform(get("/api/management/reports/least-selling")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));

            verify(reportingMapper, never()).toResponse(any(ReportingRepository.ProductSalesRow.class));
        }

        @Test
        @DisplayName("Should return multiple least selling products")
        void shouldReturnMultipleProducts() throws Exception {
            ReportingRepository.ProductSalesRow row1 = mock(ReportingRepository.ProductSalesRow.class);
            ReportingRepository.ProductSalesRow row2 = mock(ReportingRepository.ProductSalesRow.class);
            ReportingRepository.ProductSalesRow row3 = mock(ReportingRepository.ProductSalesRow.class);
            ProductSalesResponse resp1 = new ProductSalesResponse("Kiwi", new BigDecimal("3.00"), 200, false, 2L);
            ProductSalesResponse resp2 = new ProductSalesResponse("Fig", new BigDecimal("5.00"), 50, false, 1L);
            ProductSalesResponse resp3 = new ProductSalesResponse("Date", new BigDecimal("6.00"), 7, true, 0L);

            when(reportingService.leastSellingProductsOfMonth(null)).thenReturn(List.of(row1, row2, row3));
            when(reportingMapper.toResponse(row1)).thenReturn(resp1);
            when(reportingMapper.toResponse(row2)).thenReturn(resp2);
            when(reportingMapper.toResponse(row3)).thenReturn(resp3);

            mockMvc.perform(get("/api/management/reports/least-selling")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(3)))
                    .andExpect(jsonPath("$[0].name").value("Kiwi"))
                    .andExpect(jsonPath("$[1].name").value("Fig"))
                    .andExpect(jsonPath("$[2].name").value("Date"));
        }
    }

    @Nested
    @DisplayName("GET /api/management/reports/sales")
    class Sales {

        @Test
        @DisplayName("Should return sales per day for given date range")
        void shouldReturnSalesForDateRange() throws Exception {
            LocalDate start = LocalDate.of(2026, 2, 1);
            LocalDate end = LocalDate.of(2026, 2, 3);
            ReportingRepository.SalesPerDayRow row1 = mock(ReportingRepository.SalesPerDayRow.class);
            ReportingRepository.SalesPerDayRow row2 = mock(ReportingRepository.SalesPerDayRow.class);
            SalesPerDayResponse resp1 = new SalesPerDayResponse(LocalDate.of(2026, 2, 1), new BigDecimal("500.00"));
            SalesPerDayResponse resp2 = new SalesPerDayResponse(LocalDate.of(2026, 2, 2), new BigDecimal("750.00"));

            when(reportingService.saleAmountPerDay(start, end)).thenReturn(List.of(row1, row2));
            when(reportingMapper.toResponse(row1)).thenReturn(resp1);
            when(reportingMapper.toResponse(row2)).thenReturn(resp2);

            mockMvc.perform(get("/api/management/reports/sales")
                            .param("start", "2026-02-01")
                            .param("end", "2026-02-03")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].day").value("2026-02-01"))
                    .andExpect(jsonPath("$[0].totalSales").value(500.00))
                    .andExpect(jsonPath("$[1].day").value("2026-02-02"))
                    .andExpect(jsonPath("$[1].totalSales").value(750.00));

            verify(reportingService).saleAmountPerDay(start, end);
            verify(reportingMapper).toResponse(row1);
            verify(reportingMapper).toResponse(row2);
        }

        @Test
        @DisplayName("Should return empty list when no sales in range")
        void shouldReturnEmptyListWhenNoSales() throws Exception {
            LocalDate start = LocalDate.of(2026, 1, 1);
            LocalDate end = LocalDate.of(2026, 1, 31);

            when(reportingService.saleAmountPerDay(start, end)).thenReturn(List.of());

            mockMvc.perform(get("/api/management/reports/sales")
                            .param("start", "2026-01-01")
                            .param("end", "2026-01-31")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));

            verify(reportingMapper, never()).toResponse(any(ReportingRepository.SalesPerDayRow.class));
        }

        @Test
        @DisplayName("Should return 400 when start parameter is missing")
        void shouldReturn400WhenStartMissing() throws Exception {
            mockMvc.perform(get("/api/management/reports/sales")
                            .param("end", "2026-02-28")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(reportingService, never()).saleAmountPerDay(any(), any());
        }

        @Test
        @DisplayName("Should return 400 when end parameter is missing")
        void shouldReturn400WhenEndMissing() throws Exception {
            mockMvc.perform(get("/api/management/reports/sales")
                            .param("start", "2026-02-01")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(reportingService, never()).saleAmountPerDay(any(), any());
        }

        @Test
        @DisplayName("Should return 400 when both parameters are missing")
        void shouldReturn400WhenBothMissing() throws Exception {
            mockMvc.perform(get("/api/management/reports/sales")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(reportingService, never()).saleAmountPerDay(any(), any());
        }

        @Test
        @DisplayName("Should return single day sales")
        void shouldReturnSingleDaySales() throws Exception {
            LocalDate date = LocalDate.of(2026, 2, 15);
            ReportingRepository.SalesPerDayRow row = mock(ReportingRepository.SalesPerDayRow.class);
            SalesPerDayResponse response = new SalesPerDayResponse(date, new BigDecimal("1234.56"));

            when(reportingService.saleAmountPerDay(date, date)).thenReturn(List.of(row));
            when(reportingMapper.toResponse(row)).thenReturn(response);

            mockMvc.perform(get("/api/management/reports/sales")
                            .param("start", "2026-02-15")
                            .param("end", "2026-02-15")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].day").value("2026-02-15"))
                    .andExpect(jsonPath("$[0].totalSales").value(1234.56));
        }

        @Test
        @DisplayName("Should propagate InvalidOrderStateException from service")
        void shouldPropagateInvalidOrderStateException() throws Exception {
            LocalDate start = LocalDate.of(2026, 2, 28);
            LocalDate end = LocalDate.of(2026, 2, 1);

            when(reportingService.saleAmountPerDay(start, end))
                    .thenThrow(new InvalidOrderStateException("End date must be after start date"));

            mockMvc.perform(get("/api/management/reports/sales")
                            .param("start", "2026-02-28")
                            .param("end", "2026-02-01")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isConflict());

            verify(reportingService).saleAmountPerDay(start, end);
        }
    }
}