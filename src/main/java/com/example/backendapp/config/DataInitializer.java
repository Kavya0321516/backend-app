package com.example.backendapp.config;

import com.example.backendapp.entity.Product;
import com.example.backendapp.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;

    public DataInitializer(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Seeds initial sample products into MySQL if the products table is empty.
     * This provides immediate mock data for the ShopWave frontend.
     */
    @Override
    public void run(String... args) {
        if (productRepository.count() == 0) {
            List<Product> initialProducts = List.of(
                    new Product(
                            "Wireless Noise-Canceling Headphones",
                            "Premium wireless over-ear headphones with active noise cancellation and 30-hour battery life.",
                            new BigDecimal("199.99"),
                            "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=500",
                            "Electronics",
                            50
                    ),
                    new Product(
                            "Smart Fitness Watch",
                            "Water-resistant smartwatch with heart-rate monitoring, GPS, and multi-day battery.",
                            new BigDecimal("129.50"),
                            "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=500",
                            "Electronics",
                            35
                    ),
                    new Product(
                            "Ergonomic Running Shoes",
                            "Breathable, lightweight athletic running sneakers built for comfort and performance.",
                            new BigDecimal("89.90"),
                            "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=500",
                            "Footwear",
                            40
                    ),
                    new Product(
                            "Waterproof Travel Backpack",
                            "Durable waterproof backpack with padded laptop sleeve, USB charging port, and anti-theft pocket.",
                            new BigDecimal("59.99"),
                            "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=500",
                            "Accessories",
                            60
                    ),
                    new Product(
                            "Mechanical Gaming Keyboard",
                            "RGB backlit mechanical keyboard with tactile switches and detachable wrist rest.",
                            new BigDecimal("79.99"),
                            "https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=500",
                            "Electronics",
                            25
                    ),
                    new Product(
                            "Stainless Steel Insulated Water Bottle",
                            "Double-wall vacuum insulated flask keeping drinks cold for 24h or hot for 12h.",
                            new BigDecimal("24.95"),
                            "https://images.unsplash.com/photo-1602143407151-7111542de6e8?w=500",
                            "Accessories",
                            100
                    )
            );

            productRepository.saveAll(initialProducts);
        }
    }
}
