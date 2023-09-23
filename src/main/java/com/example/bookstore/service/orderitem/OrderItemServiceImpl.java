package com.example.bookstore.service.orderitem;

import com.example.bookstore.dto.order.OrderItemDto;
import com.example.bookstore.exception.EntityNotFoundException;
import com.example.bookstore.mapper.OrderItemMapper;
import com.example.bookstore.model.OrderItem;
import com.example.bookstore.model.User;
import com.example.bookstore.repository.orderitems.OrderItemsRepository;
import com.example.bookstore.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {
    private final OrderItemMapper orderItemMapper;
    private final OrderItemsRepository orderItemsRepository;
    private final UserService userService;

    @Transactional
    @Override
    public OrderItemDto findOrderItemByOrderIdAndItemId(Long orderId, Long itemId) {
        OrderItem orderItem = orderItemsRepository.findOrderItem(orderId, itemId)
                .orElseThrow(() -> new EntityNotFoundException("Can't find orderItem "
                        + "with order id: " + orderId + " and item id: " + itemId));
        User currentUser = userService.getUser();
        if (!orderItem.getOrder().getUser().equals(currentUser)) {
            throw new AccessDeniedException("User does not have access to this order item");
        }
        return orderItemMapper.toDto(orderItem);
    }

    @Transactional
    @Override
    public OrderItem save(OrderItem orderItem) {
        return orderItemsRepository.save(orderItem);
    }
}
