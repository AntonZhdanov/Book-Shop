package com.example.bookstore.dto.cartitem;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.experimental.Accessors;

@Accessors(chain = true)
public record UpdateQuantityInCartItemDto(@PositiveOrZero int quantity) {
}
