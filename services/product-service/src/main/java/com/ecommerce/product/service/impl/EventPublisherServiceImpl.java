package com.ecommerce.product.service.impl;

import com.ecommerce.product.event.ProductEvent;
import com.ecommerce.product.service.EventPublisherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 * Implementation of EventPublisherService.
 * Publishes product-related domain events.
 *
 * This implementation uses Spring’s ApplicationEventPublisher,
 * but can be extended later for Kafka or RabbitMQ integration.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EventPublisherServiceImpl implements EventPublisherService {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void publishProductEvent(ProductEvent event) {
        if (event == null) {
            log.warn("Attempted to publish a null event.");
            return;
        }

        event.setTimestamp(java.time.LocalDateTime.now());
        log.info("Publishing event: type={}, productId={}, source={}",
                event.getEventType(), event.getProductId(), event.getSource());

        try {
            eventPublisher.publishEvent(event);
        } catch (Exception ex) {
            log.error("Failed to publish product event: {}", ex.getMessage(), ex);
        }
    }
}
