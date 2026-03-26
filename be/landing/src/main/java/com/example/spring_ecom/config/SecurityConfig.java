package com.example.spring_ecom.config;

import com.example.spring_ecom.config.security.CustomAccessDeniedHandler;
import com.example.spring_ecom.config.security.CustomAuthenticationEntryPoint;
import com.example.spring_ecom.config.security.CustomPermissionEvaluator;
import com.example.spring_ecom.config.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
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
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CorsConfigurationSource corsConfigurationSource;
    private final CustomPermissionEvaluator customPermissionEvaluator;

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
                
                // Actuator endpoints
                .requestMatchers("/actuator/**").permitAll()
                
                // Test endpoints - gRPC testing
                .requestMatchers("/api/test/**").permitAll()
                .requestMatchers("/api/grpc/**").permitAll()
                
                // Public endpoints - Auth
                .requestMatchers("/api/auth/login", "/api/auth/register", "/api/auth/refresh").permitAll()
                
                // Public endpoints - Email verification
                .requestMatchers("/api/email/verify", "/api/email/resend-verification").permitAll()
                
                // Public endpoints - Products (read only)
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/products/**").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/categories/**").permitAll()
                
                // Admin endpoints - require specific permissions
                .requestMatchers("/api/admin/**").hasAuthority("ADMIN_ACCESS")
                
                // Product management - permission based
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/products/**").hasAuthority("PRODUCT_CREATE")
                .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/products/**").hasAuthority("PRODUCT_UPDATE")
                .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/products/**").hasAuthority("PRODUCT_DELETE")
                
                // Category management - permission based
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/categories/**").hasAuthority("CATEGORY_CREATE")
                .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/categories/**").hasAuthority("CATEGORY_UPDATE")
                .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/categories/**").hasAuthority("CATEGORY_DELETE")
                
                // Order management - permission based
                .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/orders/*/status").hasAuthority("ORDER_UPDATE")
                
                // User endpoints - require authentication
                .requestMatchers("/api/cart/**").authenticated()
                .requestMatchers("/api/orders/**").authenticated()
                .requestMatchers("/api/profile/**").authenticated()
                .requestMatchers("/api/users/**").authenticated()
                .requestMatchers("/api/reviews/**").authenticated()
                .requestMatchers("/api/auth/logout").authenticated()
                
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
    
    /**
     * Register custom permission evaluator for @PreAuthorize("hasPermission(...)")
     */
    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler() {
        DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
        handler.setPermissionEvaluator(customPermissionEvaluator);
        return handler;
    }
}
