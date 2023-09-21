package com.example.bookstore.service.order;

import com.example.bookstore.dto.order.MakeAnOrderDto;
import com.example.bookstore.dto.order.OrderDto;
import com.example.bookstore.dto.order.OrderItemDto;
import com.example.bookstore.dto.order.UpdateOrderStatusDto;
import com.example.bookstore.exception.EntityNotFoundException;
import com.example.bookstore.mapper.OrderItemMapper;
import com.example.bookstore.mapper.OrderMapper;
import com.example.bookstore.model.Book;
import com.example.bookstore.model.CartItem;
import com.example.bookstore.model.Order;
import com.example.bookstore.model.OrderItem;
import com.example.bookstore.model.ShoppingCart;
import com.example.bookstore.model.User;
import com.example.bookstore.repository.book.BookRepository;
import com.example.bookstore.repository.order.OrderRepository;
import com.example.bookstore.service.orderitem.OrderItemService;
import com.example.bookstore.service.shoppingcart.ShoppingCartService;
import com.example.bookstore.service.user.UserService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final ShoppingCartService shoppingCartService;
    private final UserService userService;
    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;
    private final OrderItemMapper orderItemMapper;
    private final OrderItemService orderItemService;
    private final BookRepository bookRepository;

    @Transactional
    @Override
    public OrderDto makeOrder(MakeAnOrderDto makeAnOrderDto) {
        ShoppingCart shoppingCart = shoppingCartService.getShoppingCartModel();
        Order order = orderMapper.toOrderFromCart(shoppingCart);
        order.setShippingAddress(makeAnOrderDto.shippingAddress());
        order.setOrderDate(LocalDateTime.now());
        order.setOrderStatus(Order.OrderStatus.NEW);
        BigDecimal total = orderMapper.calculateTotal(shoppingCart);
        order.setTotal(total);
        Set<OrderItem> orderItems = getOrderItemsFromCart(shoppingCart);
        orderItems.forEach(orderItem -> orderItem.setOrder(order));
        order.setOrderItems(orderItems);
        Order savedOrder = orderRepository.save(order);
        shoppingCartService.confirmPurchase(shoppingCart);
        return orderMapper.toDto(savedOrder);
    }

    @Transactional
    @Override
    public void updateOrderStatus(Long id, UpdateOrderStatusDto updateOrderStatusDto) {
        Order orderById = getOrderById(id);
        orderById.setOrderStatus(updateOrderStatusDto.orderStatus());
        orderRepository.save(orderById);
    }

    @Transactional
    @Override
    public List<OrderItemDto> getOrderItems(Long id) {
        return getOrderById(id)
                .getOrderItems()
                .stream()
                .map(orderItemMapper::toDto)
                .toList();
    }

    @Transactional
    @Override
    public List<OrderDto> getOrderHistory(Pageable pageable) {
        User user = userService.getUser();
        Page<Order> orderPage = orderRepository.findByUser(user, pageable);
        return orderPage
                .stream()
                .map(orderMapper::toDto)
                .toList();
    }

    @Transactional
    @Override
    public OrderItemDto getOrderItem(Long orderId, Long itemId) {
        return orderItemService.findOrderItemByOrderIdAndItemId(orderId, itemId);
    }

    private Set<OrderItem> getOrderItemsFromCart(ShoppingCart shoppingCart) {
        return shoppingCart.getCartItems()
                .stream()
                .map(cartItem -> orderItemMapper.convertItem(cartItem,
                        getBookFromCartItem(cartItem)))
                .collect(Collectors.toSet());
    }

    private Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(()
                -> new EntityNotFoundException("Can't find order by id: " + orderId));
    }

    private Book getBookFromCartItem(CartItem cartItem) {
        return bookRepository.findById(cartItem.getBook().getId()).orElseThrow(()
                -> new EntityNotFoundException("Can't find book by id: "
                           + cartItem.getBook().getId()));
    }
}
