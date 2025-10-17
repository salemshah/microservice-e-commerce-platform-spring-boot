package com.ecommerce.auth.service;

import com.ecommerce.auth.dto.RoleRequest;
import com.ecommerce.auth.dto.UserResponse;
import com.ecommerce.auth.entity.AuditLog;
import com.ecommerce.auth.entity.Role;
import com.ecommerce.auth.entity.User;
import com.ecommerce.auth.mapper.UserMapper;
import com.ecommerce.auth.repository.AuditLogRepository;
import com.ecommerce.auth.repository.RoleRepository;
import com.ecommerce.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final AuditLogRepository auditLogRepository;


    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toUserResponse)
                .toList();
    }

    public UserResponse getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userMapper.toUserResponse(user);
    }

    public void updateUserStatus(UUID id, boolean active) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setActive(active);
        userRepository.save(user);
    }

    public void assignRoleToUser(UUID id, String roleName) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        user.getRoles().clear();
        user.getRoles().add(role);
        userRepository.save(user);
    }

    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }

    // =============================
    // 🧩 ROLE MANAGEMENT
    // =============================

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public void createRole(RoleRequest request) {
        if (roleRepository.existsByName(request.getName())) {
            throw new RuntimeException("Role already exists: " + request.getName());
        }

        Role role = new Role();
        role.setName(request.getName());
        role.setDescription(request.getDescription());
        roleRepository.save(role);
    }

    public void deleteRole(String name) {
        Role role = roleRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Role not found: " + name));
        roleRepository.delete(role);
    }

    // =============================
    // AUDIT LOGS (Optional)
    // =============================

    public List<AuditLog> getAuditLogs() {
        return auditLogRepository.findAll()
                .stream()
                .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
                .toList();
    }

}
