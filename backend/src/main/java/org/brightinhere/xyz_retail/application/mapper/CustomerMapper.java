package org.brightinhere.xyz_retail.application.mapper;

import org.brightinhere.xyz_retail.application.dto.CustomerResponse;
import org.brightinhere.xyz_retail.domain.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public CustomerResponse toResponse(Customer customer) {
        if (customer == null) return null;

        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getMobileNumber(),
                customer.getEmail()
        );
    }
}
