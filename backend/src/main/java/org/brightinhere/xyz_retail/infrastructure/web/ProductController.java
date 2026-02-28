package org.brightinhere.xyz_retail.infrastructure.web;

import lombok.RequiredArgsConstructor;
import org.brightinhere.xyz_retail.application.dto.ProductResponse;
import org.brightinhere.xyz_retail.application.mapper.ProductMapper;
import org.brightinhere.xyz_retail.application.service.ProductService;
import org.brightinhere.xyz_retail.domain.Product;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductMapper productMapper;

    @GetMapping("/api/products/search")
    public List<ProductResponse> searchProducts(@RequestParam(name = "q") String query) {

        List<Product> product = productService.search(query);
        return product.stream()
                .map(productMapper::toResponse)
                .toList();
    }
}