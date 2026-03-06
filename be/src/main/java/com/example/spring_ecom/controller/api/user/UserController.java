package com.example.spring_ecom.controller.api.user;

import com.example.spring_ecom.controller.api.user.model.UserRequestMapper;
import com.example.spring_ecom.controller.api.user.model.UserResponse;
import com.example.spring_ecom.controller.api.user.model.UserResponseMapper;
import com.example.spring_ecom.core.response.ValueResponse;
import com.example.spring_ecom.service.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController implements UserAPI {
    private final UserUseCase userUseCase;
//    private final UserRequestMapper requestMapper;
    private final UserResponseMapper responseMapper;

    @Override
    public ValueResponse<UserResponse> findById(String userId) {
        UserResponse response = userUseCase.findByUserId(Long.parseLong(userId))
                .map(responseMapper::toResDto)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ValueResponse.success(response);
    }
}
