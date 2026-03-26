package com.example.spring_ecom.controller.api.auth.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @NotBlank(message = "Username cannot be blank")
    @Size(min = 1, message = "Username should have at least 1 character")
    String username,

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be like user@gmail.com")
    String email,

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 1, message = "Password should have at least 1 character")
    String password,
    
    String firstName,
    String lastName,
    String phoneNumber
) {
}
