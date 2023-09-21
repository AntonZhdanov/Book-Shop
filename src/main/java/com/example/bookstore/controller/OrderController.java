package com.example.bookstore.controller;

import com.example.bookstore.dto.order.MakeAnOrderDto;
import com.example.bookstore.dto.order.OrderDto;
import com.example.bookstore.dto.order.OrderItemDto;
import com.example.bookstore.dto.order.UpdateOrderStatusDto;
import com.example.bookstore.service.order.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Order management", description = "Endpoints for managing orders")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/orders")
public class OrderController {
    private final OrderService orderService;

    @Operation(summary = "Get a user's order history")
    @GetMapping
    public List<OrderDto> getOrderHistory(Pageable pageable) {
        return orderService.getOrderHistory(pageable);
    }

    @Operation(summary = "Make an order")
    @PostMapping
    public OrderDto makeOrder(@RequestBody @Valid MakeAnOrderDto makeAnOrderDto) {
        return orderService.makeOrder(makeAnOrderDto);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Update order status")
    @PatchMapping("/{id}")
    public void updateOrderStatus(@PathVariable Long id,
                                  @RequestBody @Valid UpdateOrderStatusDto updateOrderStatusDto) {
        orderService.updateOrderStatus(id, updateOrderStatusDto);
    }

    @Operation(summary = "Retrieve all OrderItems for a specific order")
    @GetMapping("/{orderId}/items")
    public List<OrderItemDto> getAllOrderItems(@PathVariable Long orderId) {
        return orderService.getOrderItems(orderId);
    }

    @Operation(summary = "Retrieve a specific OrderItem within an order")
    @GetMapping("/{orderId}/items/{itemId}")
    public OrderItemDto getOrderItemById(@PathVariable Long orderId, @PathVariable Long itemId) {
        return orderService.getOrderItem(orderId, itemId);
    }
}
