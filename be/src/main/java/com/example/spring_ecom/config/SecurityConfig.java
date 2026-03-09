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
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
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
                // Swagger/OpenAPI endpoints
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                
                // Public endpoints - Auth
                .requestMatchers("/v1/api/auth/login", "/v1/api/auth/register", "/v1/api/auth/refresh").permitAll()
                
                // Public endpoints - Products (read only)
                .requestMatchers(HttpMethod.GET, "/v1/api/products/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/v1/api/categories/**").permitAll()
                
                // Admin only endpoints
                .requestMatchers("/v1/api/admin/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/v1/api/products/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/v1/api/products/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/v1/api/products/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/v1/api/categories/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/v1/api/categories/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/v1/api/categories/**").hasRole("ADMIN")
                
                // User endpoints - require authentication
                .requestMatchers("/v1/api/cart/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/v1/api/orders/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/v1/api/profile/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/v1/api/auth/logout").hasAnyRole("USER", "ADMIN")
                
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
