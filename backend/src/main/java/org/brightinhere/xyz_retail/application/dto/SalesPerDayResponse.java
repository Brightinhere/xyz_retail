package org.brightinhere.xyz_retail.application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SalesPerDayResponse(
        LocalDate day,
        BigDecimal totalSales
) {
}