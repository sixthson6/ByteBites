package com.tech.notificationservice.service;

import com.tech.notificationservice.event.OrderPlacedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {

    @RabbitListener(queues = "notificationQueue")
    public void handleOrderPlacedEvent(OrderPlacedEvent event) {
        // Simulate sending email/push notification
        log.info("Order Placed Event Received for Order Number: {}", event.getOrderNumber());
        // In a real application, you would integrate with an email/push notification service here
    }
}
