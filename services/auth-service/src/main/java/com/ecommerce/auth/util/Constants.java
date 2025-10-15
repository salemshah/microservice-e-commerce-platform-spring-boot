package com.ecommerce.auth.util;

public final class Constants {

    private Constants() {
        // Prevent instantiation
    }

    // === JWT Related ===
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String AUTH_HEADER = "Authorization";

    // === Roles ===
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_CUSTOMER = "CUSTOMER";

    // === Messages ===
    public static final String USER_ALREADY_EXISTS = "Email is already registered.";
    public static final String USER_NOT_FOUND = "User not found.";
    public static final String INVALID_CREDENTIALS = "Invalid email or password.";
    public static final String TOKEN_EXPIRED = "Token has expired.";
}
