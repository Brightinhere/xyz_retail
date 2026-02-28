package org.brightinhere.xyz_retail.application.mapper;

import org.brightinhere.xyz_retail.application.dto.CustomerResponse;
import org.brightinhere.xyz_retail.domain.Customer;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CustomerMapperTest {

    private final CustomerMapper mapper = new CustomerMapper();

    @Test
    void toResponse_whenCustomerIsNull_returnsNull() {
        assertNull(mapper.toResponse(null));
    }

    @Test
    void toResponse_mapsAllFields() {
        UUID id = UUID.randomUUID();

        Customer customer = new Customer();
        customer.setId(id);
        customer.setName("Jane Doe");
        customer.setMobileNumber("+12025550123");
        customer.setEmail("jane.doe@example.com");

        CustomerResponse response = mapper.toResponse(customer);

        assertNotNull(response);
        assertEquals(id, response.id());
        assertEquals("Jane Doe", response.name());
        assertEquals("+12025550123", response.mobileNumber());
        assertEquals("jane.doe@example.com", response.email());
    }
}