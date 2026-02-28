package org.brightinhere.xyz_retail.application.port;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public interface ReportingRepository {
    List<ProductSalesRow> topSellingProductsOfDay(LocalDate day, int limit);

    List<ProductSalesRow> leastSellingProductsOfMonth(YearMonth month, int limit);

    List<SalesPerDayRow> saleAmountPerDay(LocalDate start, LocalDate end);

    interface ProductSalesRow {
        String getProductName();

        BigDecimal getPrice();

        Integer getQuantityAvailable();

        Long getQuantitySold();
    }

    interface SalesPerDayRow {
        LocalDate getDay();

        BigDecimal getTotalSales();
    }
}
