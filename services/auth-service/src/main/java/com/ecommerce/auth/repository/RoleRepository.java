package com.ecommerce.auth.repository;

import com.ecommerce.auth.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    //Find a role by its name (eg ROLE_ADMIN, ROLE_CUSTOMER).
    Optional<Role> findByName(String name);

    /**
     * Check if a role already exists.
     */
    boolean existsByName(String name);
}
