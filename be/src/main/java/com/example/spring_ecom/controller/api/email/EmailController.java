package com.example.spring_ecom.controller.api.email;

import com.example.spring_ecom.core.response.ApiResponse;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.service.auth.email.EmailUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/email")
@RequiredArgsConstructor
public class EmailController implements EmailAPI {
    
    private final EmailUseCase emailUseCase;
    
    @Override
    public ResponseEntity<ApiResponse<String>> verifyEmail(String token) {
        emailUseCase.verifyEmail(token);
        return ResponseEntity.ok(ApiResponse.Success.of(ResponseCode.OK, "Email verified successfully", null));
    }
    
    @Override
    public ResponseEntity<ApiResponse<String>> resendVerificationEmail(String email) {
        emailUseCase.resendVerificationEmail(email);
        return ResponseEntity.ok(ApiResponse.Success.of(ResponseCode.OK, "Verification email sent successfully", null));
    }
}