package com.example.spring_ecom.config;

import com.example.spring_ecom.config.security.CustomAccessDeniedHandler;
import com.example.spring_ecom.config.security.CustomAuthenticationEntryPoint;
import com.example.spring_ecom.config.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Swagger
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()

                        // Actuator endpoints
                        .requestMatchers("/actuator/**").permitAll()

                        // Test endpoints - gRPC testing
                        .requestMatchers("/api/test/**").permitAll()

                        // Public endpoints - Auth
                        .requestMatchers("/v1/api/auth/login", "/v1/api/auth/register", "/v1/api/auth/refresh")
                        .permitAll()

                        // Public endpoints - Email verification
                        .requestMatchers("/v1/api/email/verify", "/v1/api/email/resend-verification").permitAll()

                        // Public endpoints - Products (read only)
                        .requestMatchers(HttpMethod.GET, "/v1/api/products/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/v1/api/categories/**").permitAll()

                        // Public endpoints - Coupons (read only)
                        .requestMatchers(HttpMethod.GET, "/v1/api/coupons/**").permitAll()

                        // Admin endpoints - permission checks are done at method level via @PreAuthorize
                        .requestMatchers("/v1/api/admin/**").authenticated()

                        // User endpoints (accessible by USER, SELLER, ADMIN)
                        .requestMatchers("/v1/api/cart/**").hasAnyRole("USER", "SELLER", "ADMIN")
                        .requestMatchers("/v1/api/orders/**").hasAnyRole("USER", "SELLER", "ADMIN")
                        .requestMatchers("/v1/api/profile/**").hasAnyRole("USER", "SELLER", "ADMIN")
                        .requestMatchers("/v1/api/notifications/**").hasAnyRole("USER", "SELLER", "ADMIN")
                        .requestMatchers("/v1/api/auth/logout").hasAnyRole("USER", "SELLER", "ADMIN")

                        // File endpoints - list and download are public
                        .requestMatchers(HttpMethod.GET, "/v1/api/files/list").permitAll()
                        .requestMatchers(HttpMethod.GET, "/v1/api/files/download/**").permitAll()

                        // All other requests require authentication
                        .anyRequest().authenticated())
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
