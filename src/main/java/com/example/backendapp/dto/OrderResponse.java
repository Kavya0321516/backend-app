package com.example.backendapp.dto;

import com.example.backendapp.entity.Order;
import com.example.backendapp.entity.OrderStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class OrderResponse {

    private Long orderId;
    private Long userId;
    private String userName;
    private String userEmail;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private String shippingAddress;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    private List<OrderItemResponse> items;

    public OrderResponse() {
    }

    public OrderResponse(Long orderId, Long userId, String userName, String userEmail,
                         BigDecimal totalAmount, OrderStatus status, String shippingAddress,
                         LocalDateTime createdAt, List<OrderItemResponse> items) {
        this.orderId = orderId;
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.totalAmount = totalAmount;
        this.status = status;
        this.shippingAddress = shippingAddress;
        this.createdAt = createdAt;
        this.items = items;
    }

    public static OrderResponse fromEntity(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(OrderItemResponse::fromEntity)
                .collect(Collectors.toList());

        return new OrderResponse(
                order.getId(),
                order.getUser().getId(),
                order.getUser().getName(),
                order.getUser().getEmail(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getShippingAddress(),
                order.getCreatedAt(),
                itemResponses
        );
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<OrderItemResponse> getItems() {
        return items;
    }

    public void setItems(List<OrderItemResponse> items) {
        this.items = items;
    }
}
