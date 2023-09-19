package com.example.bookstore.service.cartitem;

import com.example.bookstore.dto.cartitem.CreateCartItemRequestDto;
import com.example.bookstore.model.CartItem;
import com.example.bookstore.model.ShoppingCart;

public interface CartItemService {
    CartItem save(CreateCartItemRequestDto createCartItemRequestDto,
                  ShoppingCart shoppingCart);

    CartItem findById(Long id);

    void deleteById(Long id);
}
