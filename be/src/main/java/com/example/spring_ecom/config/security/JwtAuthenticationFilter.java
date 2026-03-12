package com.example.spring_ecom.config.security;

import com.example.spring_ecom.service.auth.token.TokenService;
import com.example.spring_ecom.service.auth.token.TokenService;
import com.example.spring_ecom.service.auth.token.TokenInfo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final TokenService tokenService;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        try {
            String token = authHeader.substring(7);
            
            // Validate access token and get session info
            TokenInfo tokenInfo = tokenService.validateAccessToken(token);
            
            List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + tokenInfo.getRole())
            );
            
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                tokenInfo.getEmail(),
                null,
                authorities
            );
            
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            
            request.setAttribute("userId", tokenInfo.getUserId());
            request.setAttribute("sessionId", tokenInfo.getSessionId());
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            log.error("Cannot set user authentication: {}", e.getMessage());
        }
        
        filterChain.doFilter(request, response);
    }
}
