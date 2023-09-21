package com.example.bookstore.dto.order;

import com.example.bookstore.model.Order;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusDto(@NotNull @JsonProperty("status") Order.OrderStatus orderStatus) {
}
