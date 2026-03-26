package com.example.spring_ecom.config.security;

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
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final TokenService tokenService;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String authHeader = request.getHeader("Authorization");
        
        if (Objects.isNull(authHeader) || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        try {
            String token = authHeader.substring(7);
            
            TokenInfo tokenInfo = tokenService.validateAccessToken(token);
            
            // Build authorities from token
            List<SimpleGrantedAuthority> authorities = buildAuthorities(tokenInfo);
            
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

    /**
     * Build authorities from TokenInfo (loaded from Redis session)
     * No database queries needed - authorities are cached in session
     */
    private List<SimpleGrantedAuthority> buildAuthorities(TokenInfo tokenInfo) {
        if (tokenInfo.getAuthorities() == null || tokenInfo.getAuthorities().isEmpty()) {
            log.debug("No authorities found for userId={}", tokenInfo.getUserId());
            return List.of();
        }
        
        return tokenInfo.getAuthorities().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
