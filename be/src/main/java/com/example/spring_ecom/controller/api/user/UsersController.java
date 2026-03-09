package com.example.spring_ecom.controller.api.user;

import com.example.spring_ecom.controller.api.user.model.UserRequest;
import com.example.spring_ecom.controller.api.user.model.UserRequestMapper;
import com.example.spring_ecom.controller.api.user.model.UserResponseMapper;
import com.example.spring_ecom.core.response.ApiResponse;
import com.example.spring_ecom.domain.user.User;
import com.example.spring_ecom.service.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UsersController implements UsersAPI {
    private final UserUseCase useCase;
    private final UserRequestMapper requestMapper;
    private final UserResponseMapper responseMapper;

    @Override
    public ApiResponse<Void> save(UserRequest request){
        User user = requestMapper.toDomain(request);
        useCase.save(user);
        return ApiResponse.Success.of();
    }
}
