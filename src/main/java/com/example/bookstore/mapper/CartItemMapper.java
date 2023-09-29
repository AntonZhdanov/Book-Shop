package com.example.bookstore.mapper;

import com.example.bookstore.config.MapperConfiguration;
import com.example.bookstore.dto.cartitem.CartItemDto;
import com.example.bookstore.dto.cartitem.CreateCartItemRequestDto;
import com.example.bookstore.model.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfiguration.class)
public interface CartItemMapper {
    @Mapping(target = "bookId", source = "book.id")
    @Mapping(target = "bookTitle", source = "book.title")
    @Mapping(target = "shoppingCartId", source = "shoppingCart.id")
    CartItemDto toDto(CartItem book);

    @Mapping(target = "book.id", source = "requestDto.bookId")
    @Mapping(target = "quantity", source = "requestDto.quantity")
    @Mapping(target = "shoppingCart", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    CartItem toModel(CreateCartItemRequestDto requestDto);
}

