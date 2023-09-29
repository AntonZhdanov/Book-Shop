package com.example.bookstore.controller;

import com.example.bookstore.dto.cartitem.CreateCartItemRequestDto;
import com.example.bookstore.dto.cartitem.UpdateQuantityInCartItemDto;
import com.example.bookstore.dto.shoppingcart.ShoppingCartDto;
import com.example.bookstore.service.shoppingcart.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "ShoppingCart management", description = "Endpoints for managing shoppingCarts")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/cart")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Retrieve user's shopping cart")
    @GetMapping
    public ShoppingCartDto getShoppingCart() {
        return shoppingCartService.getShoppingCart();
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Add cart item to the shopping cart")
    @PostMapping
    public ShoppingCartDto addNewBookToShoppingCart(@RequestBody @Valid
            CreateCartItemRequestDto createCartItemRequestDto) {
        return shoppingCartService.saveNewCartItem(createCartItemRequestDto);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Update quantity of a cart item in the shopping cart")
    @PutMapping("/cart-items/{cartItemId}")
    public ShoppingCartDto updatingQuantityOfBook(@PathVariable Long cartItemId,
                                                 @RequestBody @Valid UpdateQuantityInCartItemDto
                                                  updateQuantityInCartItemDto) {
        return shoppingCartService.updateQuantity(cartItemId, updateQuantityInCartItemDto);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Remove a cart item from the shopping cart")
    @DeleteMapping("/cart-items/{cartItemId}")
    public ShoppingCartDto deleteBookFromShoppingCart(@PathVariable Long cartItemId) {
        return shoppingCartService.deleteCartItem(cartItemId);
    }
}
