package com.tech.order_service.event;

import java.math.BigDecimal;

public class OrderItemEvent {
    private Long productId;
    private Integer quantity;
    private BigDecimal price;

    public OrderItemEvent() {
    }

    public OrderItemEvent(Long productId, Integer quantity, BigDecimal price) {
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "OrderItemEvent{" +
               "productId=" + productId +
               ", quantity=" + quantity +
               ", price=" + price +
               '}';
    }
}
