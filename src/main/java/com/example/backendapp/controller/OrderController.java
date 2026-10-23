package com.example.backendapp.controller;

import com.example.backendapp.dto.OrderRequest;
import com.example.backendapp.dto.OrderResponse;
import com.example.backendapp.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Create a new customer order.
     * POST /api/orders
     *
     * @param request validated order payload
     * @return 201 CREATED with OrderResponse
     */
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request) {
        OrderResponse response = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieve order details by order ID.
     * GET /api/orders/{id}
     *
     * @param id order ID
     * @return 200 OK with OrderResponse
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        OrderResponse response = orderService.getOrderById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieve all orders for a specific user.
     * GET /api/orders/user/{userId}
     *
     * @param userId user ID
     * @return 200 OK with list of OrderResponses
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponse>> getOrdersByUserId(@PathVariable Long userId) {
        List<OrderResponse> responses = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(responses);
    }
}
