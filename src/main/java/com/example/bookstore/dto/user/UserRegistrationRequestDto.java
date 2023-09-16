package com.example.bookstore.dto.user;

import com.example.bookstore.service.FieldMatch;
import com.example.bookstore.service.PasswordValidator;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

@FieldMatch(
        field = "password",
        fieldMatch = "repeatPassword",
        message = "Passwords don't match!"
)
public record UserRegistrationRequestDto(
        @Email String email,
        @NotEmpty @PasswordValidator String password,
        String repeatPassword,
        @NotEmpty String firstName,
        @NotEmpty String lastName,
        @NotEmpty String shippingAddress
) {}
