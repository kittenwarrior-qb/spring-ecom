package com.example.spring_ecom.controller.api.email;

import com.example.spring_ecom.core.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Email", description = "Email related endpoints")
public interface EmailAPI {
    
    @PostMapping("/verify")
    @Operation(
        summary = "Verify email with token",
        description = "Verify user's email address using the verification token sent via email"
    )
    ResponseEntity<ApiResponse<String>> verifyEmail(
        @Parameter(description = "Email verification token", required = true)
        @RequestParam String token
    );
    
    @PostMapping("/resend-verification")
    @Operation(
        summary = "Resend verification email",
        description = "Resend email verification to the specified email address"
    )
    ResponseEntity<ApiResponse<String>> resendVerificationEmail(
        @Parameter(description = "Email address to resend verification", required = true)
        @RequestParam String email
    );
}