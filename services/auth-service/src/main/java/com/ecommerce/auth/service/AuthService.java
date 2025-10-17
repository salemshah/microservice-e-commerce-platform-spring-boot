package com.ecommerce.auth.service;

import com.ecommerce.auth.audit.AuditableAction;
import com.ecommerce.auth.dto.*;
import com.ecommerce.auth.entity.*;
import com.ecommerce.auth.mapper.UserMapper;
import com.ecommerce.auth.repository.*;
import com.ecommerce.auth.security.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailVerificationTokenRepository verificationTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthResponse register(RegisterRequest request, HttpServletResponse response) {
        // Check if the email is already taken
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        // Determine if this is the very first user
        boolean isFirstUser = userRepository.count() == 0;

        // Choose role based on whether this is the first registration
        String roleName = isFirstUser ? "ROLE_ADMIN" : "ROLE_CUSTOMER";

        // Fetch role from DB (must exist in roles table)
        Role defaultRole = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException(roleName + " not found in database"));

        // Convert DTO → Entity
        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.getRoles().add(defaultRole);


        userRepository.save(user);

        // Generate JWT token
        String jwt = jwtService.generateJwtToken(user);

        //Map to AuthResponse
        AuthResponse authResponse = userMapper.toAuthResponse(user);

        ResponseCookie cookie = ResponseCookie.from("a_token", jwt)
                .httpOnly(true)                // not accessible via JavaScript
                .secure(true)                  // only sent over HTTPS
                .path("/")                     // cookie applies to all endpoints
                .maxAge(60 * 60)               // 1 hour
                //.sameSite("Strict")            // or "Lax" or "None" if cross-site
                .sameSite("None") //If your frontend (React, Vue, etc.)
                .build();

        // Add cookie to response header
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());


        authResponse.setToken(jwt);

        return authResponse;
    }


    @AuditableAction("LOGIN")
    public AuthResponse login(LoginRequest request, HttpServletResponse response) {
        // Authenticate credentials
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // Fetch user
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Generate tokens
        String accessToken = jwtService.generateJwtToken(user);
        String refreshToken = generateRefreshToken(user);

        // Create access token cookie
        ResponseCookie accessCookie = ResponseCookie.from("a_token", accessToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(60 * 60) // 1 hour
//                .sameSite("Strict") // use "None" if frontend is on another domain
                .sameSite("None") //If your frontend (React, Vue, etc.)
                .build();

        // Create refresh token cookie
        ResponseCookie refreshCookie = ResponseCookie.from("r_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(7 * 24 * 60 * 60) // 7 days
//                .sameSite("Strict")
                .sameSite("None") //If your frontend (React, Vue, etc.)
                .build();

        // Add cookies to response
        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        // (still returns tokens if needed for legacy compatibility)
        return new AuthResponse(accessToken + "|" + refreshToken);
    }


    @AuditableAction("PASSWORD_CHANGE")
    public void changePassword(String email, ChangePasswordRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new RuntimeException("New password must be different from the old one");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);
    }

    @AuditableAction("LOGOUT")
    @Transactional
    public void logout(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        refreshTokenRepository.deleteAllByUser(user);
    }

    private String generateRefreshToken(User user) {
        byte[] randomBytes = new byte[32];
        new SecureRandom().nextBytes(randomBytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(token)
                .expiryDate(Instant.now().plus(7, ChronoUnit.DAYS))
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshToken);
        return token;
    }

    public UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return userMapper.toUserResponse(user);
    }

    public UserResponse updateProfile(String email, UpdateProfileRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Only update provided fields
        if (request.getFirstname() != null) user.setFirstname(request.getFirstname());
        if (request.getLastname() != null) user.setLastname(request.getLastname());
        if (request.getPhoneNumber() != null) user.setPhoneNumber(request.getPhoneNumber());
        if (request.getProfileImage() != null) user.setProfileImage(request.getProfileImage());

        user.setUpdatedAt(Instant.now());
        userRepository.save(user);

        return userMapper.toUserResponse(user);
    }


    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + request.getEmail()));

        // Remove old tokens for this user
        tokenRepository.deleteByUser(user);

        // Generate secure random token
        String token = generateSecureToken();

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .user(user)
                .token(token)
                .expiryDate(Instant.now().plus(15, ChronoUnit.MINUTES))
                .used(false)
                .build();

        tokenRepository.save(resetToken);

        // (Optional) Send email with reset link
        // Example: send email containing: https://yourapp.com/reset-password?token=<token>
        System.out.println("Password reset token for " + user.getEmail() + ": " + token);
    }

    private String generateSecureToken() {
        byte[] randomBytes = new byte[24];
        new SecureRandom().nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }


    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetToken token = tokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("Invalid or expired reset token"));

        if (token.isUsed()) {
            throw new RuntimeException("This token has already been used");
        }

        if (token.getExpiryDate().isBefore(Instant.now())) {
            throw new RuntimeException("Reset token has expired");
        }

        User user = token.getUser();

        // Prevent reusing the same password
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new RuntimeException("New password must be different from the old one");
        }

        // Update user password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);

        // Mark token as used
        token.setUsed(true);
        tokenRepository.save(token);
    }


    // ===============================
    // Generate token (used on register)
    // ===============================
    public void createVerificationToken(User user) {
        String token = generateSecureToken();

        EmailVerificationToken emailToken = EmailVerificationToken.builder()
                .user(user)
                .token(token)
                .expiryDate(Instant.now().plus(24, ChronoUnit.HOURS))
                .used(false)
                .build();

        verificationTokenRepository.save(emailToken);

        // (Optional) send email with link
        // Example verification URL:
        // https://yourfrontend.com/verify-email?token=<token>
        System.out.println("Email verification token for " + user.getEmail() + ": " + token);
    }


    public void verifyEmail(String token) {
        EmailVerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid verification token"));

        if (verificationToken.isUsed()) {
            throw new RuntimeException("This token has already been used");
        }

        if (verificationToken.getExpiryDate().isBefore(Instant.now())) {
            throw new RuntimeException("Verification token has expired");
        }

        User user = verificationToken.getUser();
        user.setVerified(true);
        userRepository.save(user);

        verificationToken.setUsed(true);
        verificationTokenRepository.save(verificationToken);
    }


    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (refreshToken.isRevoked() || refreshToken.isExpired()) {
            throw new RuntimeException("Refresh token is expired or revoked");
        }

        User user = refreshToken.getUser();
        String newAccessToken = jwtService.generateJwtToken(user);

        return new AuthResponse(newAccessToken);
    }

}
