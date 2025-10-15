package com.ecommerce.auth.repository;

import com.ecommerce.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Find a user by email — used during authentication.
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if an email is already registered.
     */
    boolean existsByEmail(String email);

    /**
     * Check if a user exists by phone number.
     */
    boolean existsByPhoneNumber(String phoneNumber);
}
