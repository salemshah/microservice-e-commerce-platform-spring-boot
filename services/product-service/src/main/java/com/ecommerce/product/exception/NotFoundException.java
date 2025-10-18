package com.ecommerce.product.exception;

/**
 * Thrown when a requested resource (e.g., Product, Category) is not found.
 */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
