package com.example.bookstore.mapper;

import com.example.bookstore.config.MapperConfiguration;
import com.example.bookstore.dto.cartitem.CartItemDto;
import com.example.bookstore.dto.cartitem.CreateCartItemRequestDto;
import com.example.bookstore.model.CartItem;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfiguration.class)
public interface CartItemMapper {
    @Mapping(target = "shoppingCartId", ignore = true)
    CartItemDto toDto(CartItem book);

    @Mapping(target = "id", ignore = true)
    CartItem toModel(CreateCartItemRequestDto requestDto);

    @AfterMapping
    default void setShoppingCartId(@MappingTarget CartItemDto cartItemDto, CartItem cartItem) {
        cartItemDto.setShoppingCartId(cartItem.getShoppingCart().getId());
    }

    @AfterMapping
    default void setBookId(@MappingTarget CartItemDto cartItemDto, CartItem cartItem) {
        cartItemDto.setBookId(cartItem.getBook().getId());
    }

    @AfterMapping
    default void setBookTitle(@MappingTarget CartItemDto cartItemDto, CartItem cartItem) {
        cartItemDto.setBookTitle(cartItem.getBook().getTitle());
    }
}
