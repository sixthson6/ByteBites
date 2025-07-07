package com.tech.order_service.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderPlacedEvent {
    private Long orderId;
    private Long customerId;
    private Long restaurantId;
    private BigDecimal totalAmount;
    private LocalDateTime orderDate;
    private List<OrderItemEvent> orderItems;

    public OrderPlacedEvent() {
    }

    public OrderPlacedEvent(Long orderId, Long customerId, Long restaurantId, BigDecimal totalAmount, LocalDateTime orderDate, List<OrderItemEvent> orderItems) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.restaurantId = restaurantId;
        this.totalAmount = totalAmount;
        this.orderDate = orderDate;
        this.orderItems = orderItems;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public List<OrderItemEvent> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemEvent> orderItems) {
        this.orderItems = orderItems;
    }

    @Override
    public String toString() {
        return "OrderPlacedEvent{" +
               "orderId=" + orderId +
               ", customerId=" + customerId +
               ", restaurantId=" + restaurantId +
               ", totalAmount=" + totalAmount +
               ", orderDate=" + orderDate +
               ", orderItems=" + orderItems +
               '}';
    }
}
