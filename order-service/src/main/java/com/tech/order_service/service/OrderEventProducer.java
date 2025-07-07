package com.tech.order_service.service;

import com.tech.order_service.event.OrderPlacedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderEventProducer {

    private static final String TOPIC = "order-placed-events";

    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    public OrderEventProducer(KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendOrderPlacedEvent(OrderPlacedEvent event) {
        kafkaTemplate.send(TOPIC, event.getOrderId().toString(), event);
        System.out.println(String.format("Produced OrderPlacedEvent => %s", event));
    }
}
