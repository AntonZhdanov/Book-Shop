package com.example.bookstore.dto.cartitem;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class CreateCartItemRequestDto {
    @NotNull
    @Positive
    private Long bookId;
    @NotNull
    @Positive
    private Integer quantity;
}
