package org.brightinhere.xyz_retail.application.mapper;

import org.brightinhere.xyz_retail.application.dto.ProductSalesResponse;
import org.brightinhere.xyz_retail.application.dto.SalesPerDayResponse;
import org.brightinhere.xyz_retail.application.port.ReportingRepository;
import org.springframework.stereotype.Component;

@Component
public class ReportingMapper {

    public ProductSalesResponse toResponse(ReportingRepository.ProductSalesRow row) {
        boolean lowStock = row.getQuantityAvailable() < 10;

        return new ProductSalesResponse(
                row.getProductName(),
                row.getPrice(),
                row.getQuantityAvailable(),
                lowStock,
                row.getQuantitySold()
        );
    }

    public SalesPerDayResponse toResponse(ReportingRepository.SalesPerDayRow row) {
        return new SalesPerDayResponse(
                row.getDay(),
                row.getTotalSales()
        );
    }
}