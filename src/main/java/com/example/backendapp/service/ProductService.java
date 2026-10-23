package com.example.backendapp.service;

import com.example.backendapp.dto.ProductResponse;
import com.example.backendapp.entity.Product;
import com.example.backendapp.exception.ResourceNotFoundException;
import com.example.backendapp.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Retrieve products optionally filtered by category and search keyword.
     *
     * @param category optional category filter
     * @param search optional search string for product name
     * @return list of matching product responses
     */
    @Transactional(readOnly = true)
    public List<ProductResponse> getProducts(String category, String search) {
        List<Product> products;

        boolean hasCategory = category != null && !category.trim().isEmpty();
        boolean hasSearch = search != null && !search.trim().isEmpty();

        if (hasCategory && hasSearch) {
            products = productRepository.findByCategoryIgnoreCaseAndNameContainingIgnoreCase(
                    category.trim(), search.trim()
            );
        } else if (hasCategory) {
            products = productRepository.findByCategoryIgnoreCase(category.trim());
        } else if (hasSearch) {
            products = productRepository.findByNameContainingIgnoreCase(search.trim());
        } else {
            products = productRepository.findAll();
        }

        return products.stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Retrieve a single product by its ID.
     *
     * @param id product ID
     * @return product response DTO
     * @throws ResourceNotFoundException if product does not exist
     */
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = getProductEntity(id);
        return ProductResponse.fromEntity(product);
    }

    /**
     * Internal lookup for product entity by ID.
     *
     * @param id product ID
     * @return product entity
     * @throws ResourceNotFoundException if product does not exist
     */
    @Transactional(readOnly = true)
    public Product getProductEntity(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }
}
