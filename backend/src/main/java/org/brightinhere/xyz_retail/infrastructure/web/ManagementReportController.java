package org.brightinhere.xyz_retail.infrastructure.web;

import lombok.RequiredArgsConstructor;
import org.brightinhere.xyz_retail.application.dto.ProductSalesResponse;
import org.brightinhere.xyz_retail.application.dto.SalesPerDayResponse;
import org.brightinhere.xyz_retail.application.mapper.ReportingMapper;
import org.brightinhere.xyz_retail.application.service.ReportingService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/management/reports")
public class ManagementReportController {

    private final ReportingService reportingService;
    private final ReportingMapper reportingMapper;

    @GetMapping("/top-selling")
    public List<ProductSalesResponse> topSelling(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        return reportingService.top5SellingProductsOfDay(date)
                .stream()
                .map(reportingMapper::toResponse)
                .toList();
    }

    @GetMapping("/least-selling")
    public List<ProductSalesResponse> leastSelling(
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM")
            YearMonth month
    ) {
        return reportingService.leastSellingProductsOfMonth(month)
                .stream()
                .map(reportingMapper::toResponse)
                .toList();
    }

    @GetMapping("/sales")
    public List<SalesPerDayResponse> sales(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        return reportingService.saleAmountPerDay(start, end)
                .stream()
                .map(reportingMapper::toResponse)
                .toList();
    }
}