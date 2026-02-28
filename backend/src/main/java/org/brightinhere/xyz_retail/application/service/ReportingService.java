package org.brightinhere.xyz_retail.application.service;

import lombok.RequiredArgsConstructor;
import org.brightinhere.xyz_retail.application.port.ReportingRepository;
import org.brightinhere.xyz_retail.infrastructure.exception.InvalidOrderStateException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportingService {

    private final ReportingRepository reportingRepository;

    public List<ReportingRepository.ProductSalesRow> top5SellingProductsOfDay(LocalDate day) {
        LocalDate effectiveDay = (day == null) ? LocalDate.now() : day;
        return reportingRepository.topSellingProductsOfDay(effectiveDay, 5);
    }

    public List<ReportingRepository.ProductSalesRow> leastSellingProductsOfMonth(YearMonth month) {
        YearMonth effectiveMonth = (month == null) ? YearMonth.now() : month;
        return reportingRepository.leastSellingProductsOfMonth(effectiveMonth, 5);
    }

    public List<ReportingRepository.SalesPerDayRow> saleAmountPerDay(LocalDate startInclusive, LocalDate endInclusive) {
        if (startInclusive == null || endInclusive == null) {
            throw new InvalidOrderStateException("Start and end dates are required");
        }
        if (endInclusive.isBefore(startInclusive)) {
            throw new InvalidOrderStateException("End date must be after start date");
        }
        return reportingRepository.saleAmountPerDay(startInclusive, endInclusive);
    }
}