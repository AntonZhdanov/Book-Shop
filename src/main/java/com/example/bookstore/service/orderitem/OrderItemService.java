package com.example.bookstore.service.orderitem;

import com.example.bookstore.dto.order.OrderItemDto;
import com.example.bookstore.model.OrderItem;

public interface OrderItemService {
    OrderItemDto findOrderItemByOrderIdAndItemId(Long orderId, Long itemId);

    OrderItem save(OrderItem orderItem);
}
