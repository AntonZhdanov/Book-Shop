package com.example.bookstore.dto.cartitem;

import jakarta.validation.constraints.PositiveOrZero;

public record UpdateQuantityInCartItemDto(@PositiveOrZero int quantity) {
}
