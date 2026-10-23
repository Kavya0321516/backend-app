package com.example.backendapp.service;

import com.example.backendapp.dto.OrderItemRequest;
import com.example.backendapp.dto.OrderRequest;
import com.example.backendapp.dto.OrderResponse;
import com.example.backendapp.entity.Order;
import com.example.backendapp.entity.OrderItem;
import com.example.backendapp.entity.OrderStatus;
import com.example.backendapp.entity.Product;
import com.example.backendapp.entity.User;
import com.example.backendapp.exception.InsufficientStockException;
import com.example.backendapp.exception.ResourceNotFoundException;
import com.example.backendapp.repository.OrderRepository;
import com.example.backendapp.repository.ProductRepository;
import com.example.backendapp.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository,
                        UserRepository userRepository,
                        ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    /**
     * Create a new customer order with inventory verification and stock deduction.
     * Everything runs in a single transactional boundary.
     *
     * @param request the order payload containing user ID, shipping address, and line items
     * @return OrderResponse with calculated totals and line items
     */
    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }

        Order order = new Order(user, BigDecimal.ZERO, OrderStatus.PENDING, request.getShippingAddress().trim());
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + itemRequest.getProductId()));

            if (product.getStockQuantity() < itemRequest.getQuantity()) {
                throw new InsufficientStockException(
                        "Insufficient stock for '" + product.getName() + "'. Available: "
                                + product.getStockQuantity() + ", requested: " + itemRequest.getQuantity()
                );
            }

            // Deduct inventory stock
            product.setStockQuantity(product.getStockQuantity() - itemRequest.getQuantity());
            productRepository.save(product);

            // Calculate item subtotal
            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            totalAmount = totalAmount.add(subtotal);

            // Add line item to order
            OrderItem orderItem = new OrderItem(order, product, itemRequest.getQuantity(), product.getPrice(), subtotal);
            order.addItem(orderItem);
        }

        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);

        return OrderResponse.fromEntity(savedOrder);
    }

    /**
     * Retrieve order details by order ID.
     *
     * @param orderId order identifier
     * @return OrderResponse DTO
     */
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        return OrderResponse.fromEntity(order);
    }

    /**
     * Retrieve all orders placed by a specific user.
     *
     * @param userId user identifier
     * @return list of orders placed by the user
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(OrderResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
