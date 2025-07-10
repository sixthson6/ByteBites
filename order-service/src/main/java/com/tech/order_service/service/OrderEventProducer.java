package com.tech.order_service.service;

import com.tech.order_service.event.OrderPlacedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderEventProducer {

    private static final String TOPIC = "order-placed-events";
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderEventProducer.class);

    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    public OrderEventProducer(KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendOrderPlacedEvent(OrderPlacedEvent event) {
        try {
            kafkaTemplate.send(TOPIC, event.getOrderId().toString(), event);
            LOGGER.info(String.format("Produced OrderPlacedEvent => %s", event));
        } catch (Exception e) {
            LOGGER.error("Error sending OrderPlacedEvent to Kafka", e);
        }
    }
}
