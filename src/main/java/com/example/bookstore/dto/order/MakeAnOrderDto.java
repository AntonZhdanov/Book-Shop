package com.example.bookstore.dto.order;

import jakarta.validation.constraints.NotBlank;

public record MakeAnOrderDto(@NotBlank String shippingAddress) {
}
