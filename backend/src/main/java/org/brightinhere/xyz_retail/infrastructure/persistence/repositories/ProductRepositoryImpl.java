package org.brightinhere.xyz_retail.infrastructure.persistence.repositories;

import lombok.RequiredArgsConstructor;
import org.brightinhere.xyz_retail.application.port.ProductRepository;
import org.brightinhere.xyz_retail.domain.Product;
import org.brightinhere.xyz_retail.infrastructure.persistence.jpa.ProductSpringDataRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductSpringDataRepository jpa;

    @Override
    public Product save(Product p) {
        return jpa.save(p);
    }

    @Override
    public Optional<Product> findById(UUID productId) {
        return jpa.findById(productId);
    }

    @Override
    public List<Product> search(String query) {
        return jpa.search(query, PageRequest.of(0, 50));
    }
}