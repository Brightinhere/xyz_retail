package org.brightinhere.xyz_retail.infrastructure.persistence.repositories;

import lombok.RequiredArgsConstructor;
import org.brightinhere.xyz_retail.application.port.CustomerRepository;
import org.brightinhere.xyz_retail.domain.Customer;
import org.brightinhere.xyz_retail.infrastructure.persistence.jpa.CustomerSpringDataRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CustomerRepositoryImpl implements CustomerRepository {

    private final CustomerSpringDataRepository jpa;

    @Override
    public Customer save(Customer customer) {
        return jpa.save(customer);
    }

    @Override
    public Optional<Customer> findById(UUID id) {
        return jpa.findById(id);
    }

    @Override
    public Optional<Customer> findByMobileNumber(String mobileNumber) {
        return jpa.findByMobileNumber(mobileNumber);
    }
}