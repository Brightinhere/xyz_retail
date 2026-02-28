package org.brightinhere.xyz_retail.application.service;

import lombok.RequiredArgsConstructor;
import org.brightinhere.xyz_retail.application.port.ProductRepository;
import org.brightinhere.xyz_retail.domain.Product;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * Domain-level search. Controller/mapper will convert to response DTO later.
     */
    public List<Product> search(String query) {
        String q = (query == null) ? "" : query.trim();

        if (q.isEmpty()) {
            return List.of();
        }

        return productRepository.search(q);
    }
}