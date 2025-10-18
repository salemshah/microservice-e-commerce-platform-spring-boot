package com.ecommerce.product.listener;

import com.ecommerce.product.event.ProductEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Example listener for internal product events.
 * You can later replace this with a Kafka listener or external subscriber.
 */
@Component
@Slf4j
public class ProductEventListener {

    @EventListener
    public void handleProductEvent(ProductEvent event) {
        log.info("Received internal event: {} for product ID={}", event.getEventType(), event.getProductId());
        // TODO: implement custom logic (e.g., indexing, notification, etc.)
    }
}
