package com.example.bookstore.mapper;

import com.example.bookstore.config.MapperConfiguration;
import com.example.bookstore.dto.order.OrderDto;
import com.example.bookstore.model.Order;
import com.example.bookstore.model.ShoppingCart;
import java.math.BigDecimal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfiguration.class, uses = OrderItemMapper.class)
public interface OrderMapper {
    @Mapping(source = "user.id", target = "userId")
    OrderDto toDto(Order order);

    @Mapping(source = "cartItems", target = "orderItems")
    @Mapping(target = "total", expression = "java(calculateTotal(shoppingCart))")
    Order toOrderFromCart(ShoppingCart shoppingCart);

    default BigDecimal calculateTotal(ShoppingCart shoppingCart) {
        return shoppingCart.getCartItems().stream()
                .map(item -> item.getBook().getPrice().multiply(BigDecimal
                        .valueOf(item.getQuantity())))
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }
}
