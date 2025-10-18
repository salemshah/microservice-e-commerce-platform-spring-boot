package com.ecommerce.product.event;

import lombok.*;
import java.time.LocalDateTime;

/**
 * Represents a product-related domain event.
 * Can be sent to Kafka, RabbitMQ, or internal listeners.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductEvent {

    private Long productId;
    private String eventType; // e.g., "PRODUCT_CREATED", "PRODUCT_UPDATED", "PRODUCT_DELETED"
    private String source;    // e.g., "product-service"
    private Object payload;   // optional — full or partial product data
    private LocalDateTime timestamp;
}
