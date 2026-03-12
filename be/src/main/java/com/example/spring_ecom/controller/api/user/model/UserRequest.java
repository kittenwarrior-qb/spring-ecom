package com.example.spring_ecom.controller.api.user.model;

import com.example.spring_ecom.domain.user.UserRole;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record UserRequest (
        String username,
        String email,
        String password
) {

}
