package com.example.spring_ecom.controller.api.user;

import com.example.spring_ecom.controller.api.user.model.UserRequest;
import com.example.spring_ecom.controller.api.user.model.UserRequestMapper;
import com.example.spring_ecom.controller.api.user.model.UserResponse;
import com.example.spring_ecom.controller.api.user.model.UserResponseMapper;
import com.example.spring_ecom.core.response.ApiResponse;
import com.example.spring_ecom.core.response.ResponseCode;
import com.example.spring_ecom.domain.user.User;
import com.example.spring_ecom.service.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
        return ApiResponse.Success.of(ResponseCode.USER_CREATED);
    }

    // ?sort=fieldName,asc&page=0&size=10
    @Override
    public ApiResponse<Page<UserResponse>> findAll(Pageable pageable) {
        PageRequest pageRequest = PageRequest.of(
            pageable.getPageNumber(),
            pageable.getPageSize(),
            pageable.getSort()
        );
        Page<User> users = useCase.findAll(pageRequest);
        Page<UserResponse> response = users.map(responseMapper::toResponse);
        return ApiResponse.Success.of(ResponseCode.USER_LIST, response);
    }
}
