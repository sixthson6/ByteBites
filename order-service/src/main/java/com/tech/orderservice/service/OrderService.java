package com.tech.orderservice.service;

import com.tech.orderservice.model.Order;
import com.tech.orderservice.model.OrderLineItems;
import com.tech.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final RabbitTemplate rabbitTemplate;
    private final WebClient.Builder webClientBuilder;

    @CircuitBreaker(name = "inventory", fallbackMethod = "fallbackMethod")
    public String placeOrder(Order order) {
        order.setOrderNumber(UUID.randomUUID().toString());

        Boolean inStock = webClientBuilder.build().get()
                .uri("http://inventory-service/api/inventory/{skuCode}?quantity={quantity}",
                        order.getOrderLineItems().get(0).getSkuCode(),
                        order.getOrderLineItems().get(0).getQuantity())
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();

        if (inStock != null && inStock) {
            orderRepository.save(order);
            rabbitTemplate.convertAndSend("orderExchange", "orderRoutingKey", order.getOrderNumber());
            return "Order Placed Successfully";
        } else {
            throw new RuntimeException("Product is not in stock");
        }
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    public String fallbackMethod(Order order, Throwable t) {
        return "Order Service is experiencing issues. Please try again later.";
    }
}