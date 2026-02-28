package org.brightinhere.xyz_retail.infrastructure.persistence.repositories;

import lombok.RequiredArgsConstructor;
import org.brightinhere.xyz_retail.application.port.ReportingRepository;
import org.brightinhere.xyz_retail.infrastructure.persistence.jpa.ReportingSpringDataRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReportingRepositoryImpl implements ReportingRepository {

    private final ReportingSpringDataRepository jpa;

    @Override
    public List<ProductSalesRow> topSellingProductsOfDay(LocalDate day, int limit) {
        return jpa.topSellingProductsOfDay(day, PageRequest.of(0, limit));
    }

    @Override
    public List<ProductSalesRow> leastSellingProductsOfMonth(YearMonth month, int limit) {

        YearMonth target = month != null ? month : YearMonth.now();

        LocalDateTime start = target.atDay(1).atStartOfDay();
        LocalDateTime end = target.plusMonths(1).atDay(1).atStartOfDay();

        return jpa.leastSellingProductsOfMonth(
                start,
                end,
                PageRequest.of(0, limit)
        );
    }

    @Override
    public List<SalesPerDayRow> saleAmountPerDay(LocalDate start, LocalDate end) {
        return jpa.saleAmountPerDay(start, end);
    }
}