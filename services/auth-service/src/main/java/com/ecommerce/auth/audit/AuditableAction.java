package com.ecommerce.auth.audit;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuditableAction {
    String value();  // e.g., "LOGIN", "LOGOUT", "REGISTER", "PASSWORD_CHANGE"
}
