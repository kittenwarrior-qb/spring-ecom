package com.example.spring_ecom.controller.api.user;

import com.example.spring_ecom.controller.api.user.model.UserResponse;
import com.example.spring_ecom.controller.api.user.model.UserResponseMapper;
import com.example.spring_ecom.core.exception.BaseException;
import com.example.spring_ecom.core.response.ApiResponse;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.service.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController implements UserAPI {
    private final UserUseCase userUseCase;
    private final UserResponseMapper responseMapper;

    @Override
    public ApiResponse<UserResponse> findById(String userId) {
        UserResponse response = userUseCase.findByUserId(Long.parseLong(userId))
                .map(responseMapper::toResDto)
                .orElseThrow(() -> new BaseException(ResponseCode.USER_NOT_FOUND, "User not found"));
        return ApiResponse.Success.of(response);
    }
}
