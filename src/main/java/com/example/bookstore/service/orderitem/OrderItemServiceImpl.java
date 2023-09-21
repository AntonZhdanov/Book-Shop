package com.example.bookstore.service.orderitem;

import com.example.bookstore.dto.order.OrderItemDto;
import com.example.bookstore.exception.EntityNotFoundException;
import com.example.bookstore.mapper.OrderItemMapper;
import com.example.bookstore.model.OrderItem;
import com.example.bookstore.repository.orderitems.OrderItemsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {
    private final OrderItemMapper orderItemMapper;
    private final OrderItemsRepository orderItemsRepository;

    @Transactional
    @Override
    public OrderItemDto findOrderItemByOrderIdAndItemId(Long orderId, Long itemId) {
        return orderItemMapper.toDto(orderItemsRepository
                .findOrderItem(orderId, itemId).orElseThrow(()
                        -> new EntityNotFoundException("Can't find orderItem with order id: "
                + orderId + " and item id: " + itemId)));
    }

    @Transactional
    @Override
    public OrderItem save(OrderItem orderItem) {
        return orderItemsRepository.save(orderItem);
    }
}
