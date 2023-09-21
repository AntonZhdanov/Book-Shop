package com.example.bookstore.service.shoppingcart;

import com.example.bookstore.dto.cartitem.CreateCartItemRequestDto;
import com.example.bookstore.dto.cartitem.UpdateQuantityInCartItemDto;
import com.example.bookstore.dto.shoppingcart.ShoppingCartDto;
import com.example.bookstore.model.ShoppingCart;

public interface ShoppingCartService {
    ShoppingCartDto getShoppingCart();

    ShoppingCartDto saveNewCartItem(CreateCartItemRequestDto createCartItemRequestDto);

    ShoppingCartDto deleteCartItem(Long id);

    ShoppingCartDto updateQuantity(Long id,
                                   UpdateQuantityInCartItemDto updateQuantityInCartItemDto);

    ShoppingCart getShoppingCartModel();

    void confirmPurchase(ShoppingCart shoppingCart);
}
