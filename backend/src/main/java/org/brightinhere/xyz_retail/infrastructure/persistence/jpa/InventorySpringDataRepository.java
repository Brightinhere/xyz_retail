package org.brightinhere.xyz_retail.infrastructure.persistence.jpa;

import org.brightinhere.xyz_retail.domain.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InventorySpringDataRepository extends JpaRepository<Inventory, UUID> {
}