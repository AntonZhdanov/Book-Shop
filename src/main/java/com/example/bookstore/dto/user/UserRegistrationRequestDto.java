package com.example.bookstore.dto.user;

import com.example.bookstore.service.FieldMatch;
import com.example.bookstore.service.PasswordValidator;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
@FieldMatch(
        field = "password",
        fieldMatch = "repeatPassword",
        message = "Passwords don't match!"
)
public class UserRegistrationRequestDto {
    @Email
    private String email;
    @NotEmpty
    @PasswordValidator
    private String password;
    private String repeatPassword;
    @NotEmpty
    private String firstName;
    @NotEmpty
    private String lastName;
    @NotEmpty
    private String shippingAddress;
}
