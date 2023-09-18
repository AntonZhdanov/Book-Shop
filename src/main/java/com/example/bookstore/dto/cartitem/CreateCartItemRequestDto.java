package com.example.bookstore.dto.cartitem;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CreateCartItemRequestDto {
    @NotEmpty
    @Positive
    private Long bookId;
    @NotEmpty
    @Positive
    private Integer quantity;
}
