package com.example.bookstore.service.shoppingcart;

import com.example.bookstore.dto.cartitem.CreateCartItemRequestDto;
import com.example.bookstore.dto.cartitem.UpdateQuantityInCartItemDto;
import com.example.bookstore.dto.shoppingcart.ShoppingCartDto;
import com.example.bookstore.exception.EntityNotFoundException;
import com.example.bookstore.mapper.ShoppingCartMapper;
import com.example.bookstore.model.CartItem;
import com.example.bookstore.model.ShoppingCart;
import com.example.bookstore.model.User;
import com.example.bookstore.repository.cartitem.CartItemRepository;
import com.example.bookstore.repository.shoppingcart.ShoppingCartRepository;
import com.example.bookstore.service.cartitem.CartItemService;
import com.example.bookstore.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartMapper shoppingCartMapper;
    private final ShoppingCartRepository shoppingCartRepository;
    private final CartItemService cartItemService;
    private final UserService userService;
    private final CartItemRepository cartItemRepository;

    @Transactional
    @Override
    public ShoppingCartDto getShoppingCart() {
        return shoppingCartMapper.toDto(getUserShoppingCart());
    }

    @Transactional
    @Override
    public ShoppingCartDto saveNewCartItem(CreateCartItemRequestDto createCartItemRequestDto) {
        ShoppingCart shoppingCart = getUserShoppingCart();
        CartItem cartItem = cartItemService.save(createCartItemRequestDto, shoppingCart);
        shoppingCart.getCartItems().add(cartItem);
        shoppingCartRepository.save(shoppingCart);
        return getShoppingCart();
    }

    @Transactional
    @Override
    public ShoppingCartDto deleteCartItem(Long id) {
        User user = userService.getUser();
        cartItemService.deleteById(id, user.getId());
        ShoppingCart shoppingCart = getUserShoppingCart();
        shoppingCart.getCartItems().removeIf(cartItem -> cartItem.getId().equals(id));
        return getShoppingCart();
    }

    @Transactional
    @Override
    public ShoppingCartDto updateQuantity(Long id,
                                          UpdateQuantityInCartItemDto updateQuantityInCartItemDto) {
        CartItem cartItem = cartItemService.getById(id);
        cartItem.setQuantity(updateQuantityInCartItemDto.quantity());
        cartItemRepository.save(cartItem);
        return getShoppingCart();
    }

    private ShoppingCart getUserShoppingCart() {
        User user = userService.getUser();
        return shoppingCartRepository.findByUserId(userService.getUser().getId()).orElseThrow(() ->
                new EntityNotFoundException("Can't find cart by user id: " + user.getId()));
    }
}
