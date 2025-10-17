package com.ecommerce.auth.controller;

import com.ecommerce.auth.dto.RoleRequest;
import com.ecommerce.auth.dto.UserResponse;
import com.ecommerce.auth.service.AdminService;
import com.ecommerce.auth.util.ResponseBuilder;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin")
public class AdminController {

    private final AdminService adminService;

    /**
     * Get all users
     */
    @GetMapping("/users")
    public ResponseEntity<Object> getAllUsers() {
        return ResponseBuilder.success("User list retrieved successfully", adminService.getAllUsers());
    }

    /**
     * Get specific user by ID
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable UUID id) {
        UserResponse user = adminService.getUserById(id);
        return ResponseBuilder.success("User details retrieved successfully", user);
    }

    /**
     * Update user active/inactive status
     */
    @PutMapping("/users/{id}/status")
    public ResponseEntity<Object> updateUserStatus(@PathVariable UUID id, @RequestParam boolean active) {
        adminService.updateUserStatus(id, active);
        return ResponseBuilder.success("User status updated successfully", null);
    }

    /**
     * Assign a new role to a user
     */
    @PutMapping("/users/{id}/role")
    public ResponseEntity<Object> assignUserRole(@PathVariable UUID id, @RequestParam String roleName) {
        adminService.assignRoleToUser(id, roleName);
        return ResponseBuilder.success("User role updated successfully", null);
    }

    /**
     * Delete a user account
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable UUID id) {
        adminService.deleteUser(id);
        return ResponseBuilder.success("User deleted successfully", null);
    }


    // =============================
    // ROLE MANAGEMENT
    // =============================

    @GetMapping("/roles")
    public ResponseEntity<Object> getAllRoles() {
        return ResponseBuilder.success("Roles retrieved successfully", adminService.getAllRoles());
    }

    @PostMapping("/roles")
    public ResponseEntity<Object> createRole(@RequestBody RoleRequest request) {
        adminService.createRole(request);
        return ResponseBuilder.success("Role created successfully", null);
    }

    @DeleteMapping("/roles/{name}")
    public ResponseEntity<Object> deleteRole(@PathVariable String name) {
        adminService.deleteRole(name);
        return ResponseBuilder.success("Role deleted successfully", null);
    }

    // =============================
    // AUDIT LOGS (Optional)
    // =============================

    @GetMapping("/audit-logs")
    public ResponseEntity<Object> getAuditLogs() {
        return ResponseBuilder.success("Audit logs retrieved successfully", adminService.getAuditLogs());
    }
}
