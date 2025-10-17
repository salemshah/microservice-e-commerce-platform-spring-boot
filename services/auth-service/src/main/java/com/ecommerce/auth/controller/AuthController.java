package com.ecommerce.auth.controller;

import com.ecommerce.auth.dto.*;
import com.ecommerce.auth.service.AuthService;
import com.ecommerce.auth.util.ResponseBuilder;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Object> register(@Valid @RequestBody RegisterRequest request, HttpServletResponse HTSResponse) {
        AuthResponse response = authService.register(request, HTSResponse);
        return ResponseBuilder.success("User registered successfully", response);
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        AuthResponse AuthResponse = authService.login(request, response);
        return ResponseBuilder.success("Login successful", AuthResponse);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<Object> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshToken(request);
        return ResponseBuilder.success("Token refreshed successfully", response);
    }

    @GetMapping("/me")
    public ResponseEntity<Object> getCurrentUser(Authentication authentication) {
        String email = authentication.getName();  // Extract user email from JWT
        UserResponse user = authService.getCurrentUser(email);
        return ResponseBuilder.success("User profile retrieved successfully", user);
    }

    @PutMapping("/update-profile")
    public ResponseEntity<Object> updateProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        String email = authentication.getName(); // extract email from JWT
        UserResponse updatedUser = authService.updateProfile(email, request);
        return ResponseBuilder.success("Profile updated successfully", updatedUser);
    }


    @PostMapping("/change-password")
    public ResponseEntity<Object> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        String email = authentication.getName(); // Extract email from JWT
        authService.changePassword(email, request);
        return ResponseBuilder.success("Password changed successfully", null);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Object> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseBuilder.success("Password reset link sent successfully", null);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Object> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseBuilder.success("Password reset successfully", null);
    }

    @GetMapping("/verify-email")
    public ResponseEntity<Object> verifyEmail(@RequestParam("token") String token) {
        authService.verifyEmail(token);
        return ResponseBuilder.success("Email verified successfully", null);
    }

    @PostMapping("/logout")
    public ResponseEntity<Object> logout(Authentication authentication) {
        String email = authentication.getName(); // user email from JWT
        authService.logout(email);
        return ResponseBuilder.success("Logged out successfully", null);
    }
}
