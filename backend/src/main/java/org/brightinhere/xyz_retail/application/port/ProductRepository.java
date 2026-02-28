package org.brightinhere.xyz_retail.application.port;

import org.brightinhere.xyz_retail.domain.Product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {
    Optional<Product> findById(UUID productId);

    Product save(Product p);

    List<Product> search(String query);
}

