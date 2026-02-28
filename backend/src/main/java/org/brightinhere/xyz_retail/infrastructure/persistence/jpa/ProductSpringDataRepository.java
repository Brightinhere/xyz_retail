package org.brightinhere.xyz_retail.infrastructure.persistence.jpa;

import org.brightinhere.xyz_retail.domain.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ProductSpringDataRepository extends JpaRepository<Product, UUID> {

    @Query("""
            select p
            from Product p
            where lower(p.name) like lower(concat('%', :q, '%'))
               or lower(p.description) like lower(concat('%', :q, '%'))
            order by p.name asc
            """)
    List<Product> search(@Param("q") String query, Pageable pageable);
}