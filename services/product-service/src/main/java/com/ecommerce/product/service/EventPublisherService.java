package com.ecommerce.product.service;

import com.ecommerce.product.event.ProductEvent;

/**
 * EventPublisherService
 *
 * Publishes product-related events to a message broker (Kafka, RabbitMQ, etc.)
 * or internally within the same service using Spring's ApplicationEventPublisher.
 */
public interface EventPublisherService {

    /**
     * Publish a product domain event (create, update, delete, etc.).
     *
     * @param event product event object
     */
    void publishProductEvent(ProductEvent event);
}
