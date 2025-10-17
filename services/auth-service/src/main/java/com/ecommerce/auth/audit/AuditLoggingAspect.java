package com.ecommerce.auth.audit;

import com.ecommerce.auth.entity.User;
import com.ecommerce.auth.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditLoggingAspect {

    private final AuditLogService auditLogService;

    // Pointcut for all methods annotated with @AuditableAction
    @Pointcut("@annotation(auditableAction)")
    public void auditableMethods(AuditableAction auditableAction) {}

    @AfterReturning(pointcut = "auditableMethods(auditableAction)", returning = "result", argNames = "joinPoint,auditableAction,result")
    public void logAuditAction(JoinPoint joinPoint, AuditableAction auditableAction, Object result) {
        try {
            String action = auditableAction.value();

            // Get current authenticated user (if exists)
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String performedBy = (auth != null && auth.isAuthenticated()) ? auth.getName() : "SYSTEM";

            // Build a readable description
            String method = joinPoint.getSignature().getName();
            String description = String.format("Action: %s executed via %s() at %s", action, method, Instant.now());

            auditLogService.log(action, description, performedBy);
        } catch (Exception e) {
            System.err.println("Audit logging failed: " + e.getMessage());
        }
    }
}
