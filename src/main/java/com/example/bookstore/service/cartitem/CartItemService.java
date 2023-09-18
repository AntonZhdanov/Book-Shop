package com.example.bookstore.service.cartitem;

import com.example.bookstore.dto.cartitem.CreateCartItemRequestDto;
import com.example.bookstore.model.CartItem;

public interface CartItemService {
    CartItem save(CreateCartItemRequestDto createCartItemRequestDto);

    CartItem findById(Long id);

    void deleteById(Long id);
}
