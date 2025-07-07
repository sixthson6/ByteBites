package com.tech.order_service.controller;

import com.tech.order_service.entity.Order;
import com.tech.order_service.entity.OrderItem;
import com.tech.order_service.event.OrderPlacedEvent;
import com.tech.order_service.event.OrderItemEvent;
import com.tech.order_service.repository.OrderRepository;
import com.tech.order_service.service.OrderEventProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderEventProducer orderEventProducer;

    @PostMapping
    public Order createOrder(@RequestBody Order order) {
        order.setOrderDate(LocalDateTime.now());
        for (OrderItem item : order.getOrderItems()) {
            item.setOrder(order);
        }
        Order savedOrder = orderRepository.save(order);

        List<OrderItemEvent> orderItemEvents = savedOrder.getOrderItems().stream()
                .map(item -> new OrderItemEvent(item.getProductId(), item.getQuantity(), item.getPrice()))
                .collect(Collectors.toList());

        OrderPlacedEvent orderPlacedEvent = new OrderPlacedEvent(
                savedOrder.getId(),
                savedOrder.getCustomerId(),
                savedOrder.getRestaurantId(),
                savedOrder.getTotalAmount(),
                savedOrder.getOrderDate(),
                orderItemEvents
        );
        orderEventProducer.sendOrderPlacedEvent(orderPlacedEvent);

        return savedOrder;
    }

    @GetMapping
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return orderRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable Long id, @RequestBody Order orderDetails) {
        return orderRepository.findById(id)
                .map(order -> {
                    order.setCustomerId(orderDetails.getCustomerId());
                    order.setRestaurantId(orderDetails.getRestaurantId());
                    order.setTotalAmount(orderDetails.getTotalAmount());
                    order.setStatus(orderDetails.getStatus());
                    return ResponseEntity.ok(orderRepository.save(order));
                }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        return orderRepository.findById(id)
                .map(order -> {
                    orderRepository.delete(order);
                    return ResponseEntity.ok().<Void>build();
                }).orElse(ResponseEntity.notFound().build());
    }
}
