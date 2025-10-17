package com.ecommerce.auth.service;

import com.ecommerce.auth.entity.AuditLog;
import com.ecommerce.auth.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public void log(String action, String description, String performedBy) {
        AuditLog log = AuditLog.builder()
                .action(action)
                .description(description)
                .performedBy(performedBy != null ? performedBy : "SYSTEM")
                .timestamp(Instant.now())
                .build();
        auditLogRepository.save(log);
    }
}
