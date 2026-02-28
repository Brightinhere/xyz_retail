package org.brightinhere.xyz_retail.infrastructure.persistence.jpa;

import org.brightinhere.xyz_retail.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CustomerSpringDataRepository extends JpaRepository<Customer, UUID> {
    Optional<Customer> findByMobileNumber(String mobileNumber);
}