package org.brightinhere.xyz_retail.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record PlaceOrderRequest(
        @NotBlank String customerName,
        @NotBlank String mobileNumber,
        String email
) {
}