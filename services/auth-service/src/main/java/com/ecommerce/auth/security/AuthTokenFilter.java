package com.ecommerce.auth.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Log4j2
public class AuthTokenFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String jwt = resolveToken(request);

        if (jwt == null || jwt.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            if (jwtService.validateJwtToken(jwt)) {
                var claims = jwtService.extractAllClaims(jwt);
                var email = claims.getSubject();

                Object rolesClaim = claims.get("role");
                log.debug("JWT Filter: subject={} role={}", email, rolesClaim);
                List<SimpleGrantedAuthority> authorities = new ArrayList<>();

                if (rolesClaim instanceof List<?> rolesList) {
                    for (Object r : rolesList) {
                        String roleStr = r.toString();
                        if (!roleStr.startsWith("ROLE_")) {
                            roleStr = "ROLE_" + roleStr;
                        }
                        authorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority(roleStr));
                    }
                } else if (rolesClaim instanceof String roleStr) {
                    if (!roleStr.startsWith("ROLE_")) {
                        roleStr = "ROLE_" + roleStr;
                    }
                    authorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority(roleStr));
                }

                var userDetails = org.springframework.security.core.userdetails.User
                        .withUsername(claims.getSubject())
                        .password("")
                        .authorities(authorities)
                        .build();

                var auth = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(auth);


                log.debug("JWT Filter: context set for {} with authorities {}", email, authorities);
            }
        } catch (ExpiredJwtException e) {
            log.warn("JWT expired: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        } catch (SignatureException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        } catch (MalformedJwtException e) {
            log.error("Malformed JWT: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        } catch (Exception e) {
            log.error("Unexpected JWT filter error: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extract JWT token from either the Authorization header or access_token cookie.
     */
    private String resolveToken(HttpServletRequest request) {

        // 1: Try cookie (a_token)
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("a_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        // 2: Try Authorization header
        String headerAuth = request.getHeader("Authorization");
        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return null;
    }
}