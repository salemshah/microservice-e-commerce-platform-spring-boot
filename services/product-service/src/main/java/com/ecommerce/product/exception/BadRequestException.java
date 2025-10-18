package com.ecommerce.product.exception;

/**
 * Thrown when a client sends invalid data or a business rule is violated.
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
