package com.tech.restaurant_service.listener;

import com.tech.order_service.event.OrderPlacedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderPlacedEventListener {

    @KafkaListener(topics = "order-placed-events", groupId = "restaurant-group")
    public void handleOrderPlacedEvent(OrderPlacedEvent event) {
        System.out.println("Restaurant Service - Received OrderPlacedEvent: " + event.getOrderId());
    }
}
