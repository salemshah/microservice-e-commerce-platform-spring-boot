package com.ecommerce.auth.config;

import com.ecommerce.auth.entity.Role;
import com.ecommerce.auth.repository.RoleRepository;
import com.ecommerce.auth.security.AuthTokenFilter;
import com.ecommerce.auth.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

// Marks this class as a configuration class, allowing Spring to detect it
// and load its beans during application startup.
@Configuration

// Enables Spring Security’s web security support.
// This annotation activates the web-based security features and allows
// you to customize authentication and authorization for HTTP requests.
@EnableWebSecurity

// Enables method-level security annotations like @PreAuthorize, @PostAuthorize, @Secured, etc.
// The 'prePostEnabled = true' flag specifically allows the use of @PreAuthorize and @PostAuthorize
// annotations in your controllers and services.
@EnableMethodSecurity(prePostEnabled = true)

// Lombok annotation that generates a constructor for all final fields.
// It’s commonly used with dependency injection to automatically inject
// required beans into this configuration class.
@RequiredArgsConstructor
public class SecurityConfig {

    //    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthTokenFilter authTokenFilter;


    /**
     * DAO authentication provider (for UserDetailsService)
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }


    /**
     * Main security filter chain
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())

                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS).permitAll()

                        // Public auth endpoints
                        .requestMatchers("/api/auth/login", "/api/auth/register", "/api/auth/refresh-token").permitAll()

                        // Admin area
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        //exampl multi role
                        .requestMatchers("/api/admin/users").hasAnyRole("CUSTOMER", "ADMIN")


                        .requestMatchers("/actuator/**").hasRole("ADMIN")


                        // Swagger and docs
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/api/docs/**"
                        ).permitAll()

                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    CommandLineRunner initRoles(RoleRepository roleRepository) {
        return args -> {
            if (roleRepository.count() == 0) {
                roleRepository.saveAll(List.of(
                        Role.builder().name("ROLE_ADMIN").description("Administrator role").build(),
                        Role.builder().name("ROLE_CUSTOMER").description("Regular customer role").build(),
                        Role.builder().name("ROLE_SELLER").description("User who can sell products").build(),
                        Role.builder().name("ROLE_MANAGER").description("Manager with limited admin privileges").build()
                ));
            }
        };
    }

}
