package org.brightinhere.xyz_retail.application.port;

import org.brightinhere.xyz_retail.domain.Customer;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository {
    Customer save(Customer customer);

    Optional<Customer> findById(UUID id);

    Optional<Customer> findByMobileNumber(String mobileNumber);
}