package org.brightinhere.xyz_retail.application.dto;

import java.util.UUID;

public record CustomerResponse(
        UUID id,
        String name,
        String mobileNumber,
        String email
) {
}
