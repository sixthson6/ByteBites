package com.tech.orderservice.service;

import com.tech.orderservice.model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    Order saveOrder(Order order);
    Optional<Order> findById(Long id);
    List<Order> findAll();
    void deleteById(Long id);
}
