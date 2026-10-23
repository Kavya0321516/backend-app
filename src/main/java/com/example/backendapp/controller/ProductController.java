package com.example.backendapp.controller;

import com.example.backendapp.dto.ProductResponse;
import com.example.backendapp.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Retrieve a list of products with optional filters.
     * GET /api/products
     * GET /api/products?category=electronics
     * GET /api/products?search=wireless
     *
     * @param category optional category filter
     * @param search optional name search keyword
     * @return 200 OK with list of products
     */
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search) {
        List<ProductResponse> products = productService.getProducts(category, search);
        return ResponseEntity.ok(products);
    }

    /**
     * Retrieve single product details by product ID.
     * GET /api/products/{id}
     *
     * @param id product ID
     * @return 200 OK with product details
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        ProductResponse product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }
}
