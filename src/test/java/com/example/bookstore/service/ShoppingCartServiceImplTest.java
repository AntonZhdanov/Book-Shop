package com.example.bookstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import com.example.bookstore.service.shoppingcart.ShoppingCartServiceImpl;
import com.example.bookstore.service.user.UserService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ShoppingCartServiceImplTest {
    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;
    @Mock
    private ShoppingCartMapper shoppingCartMapper;
    @Mock
    private ShoppingCartRepository shoppingCartRepository;
    @Mock
    private CartItemService cartItemService;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private UserService userService;

    private User mockUser;
    private ShoppingCart mockCart;
    private CartItem mockCartItem;
    private ShoppingCartDto mockCartDto;
    private CreateCartItemRequestDto requestDto;
    private UpdateQuantityInCartItemDto updateDto;

    @BeforeEach
    public void setUp() {
        mockUser = new User();
        mockUser.setId(1L);

        mockCart = new ShoppingCart();

        mockCartItem = new CartItem();

        mockCartDto = new ShoppingCartDto();

        requestDto = new CreateCartItemRequestDto();

        updateDto = new UpdateQuantityInCartItemDto(5);
    }

    @Test
    @DisplayName("Get shopping cart with valid input returns DTO")
    public void getShoppingCart_Valid_ReturnsDto() {
        when(userService.getUser()).thenReturn(mockUser);
        when(shoppingCartRepository.findByUserId(mockUser.getId()))
                .thenReturn(Optional.of(mockCart));
        when(shoppingCartMapper.toDto(mockCart)).thenReturn(mockCartDto);

        ShoppingCartDto result = shoppingCartService.getShoppingCart();

        assertEquals(mockCartDto, result);
    }

    @Test
    @DisplayName("Save new cart item with valid input saves and returns updated cart")
    public void saveNewCartItem_Valid_SavesAndReturnsUpdatedCart() {
        mockUser.setId(2L);

        when(userService.getUser()).thenReturn(mockUser);
        when(shoppingCartRepository.findByUserId(mockUser.getId()))
                .thenReturn(Optional.of(mockCart));
        when(cartItemService.save(requestDto, mockCart)).thenReturn(mockCartItem);
        when(shoppingCartMapper.toDto(mockCart)).thenReturn(mockCartDto);

        ShoppingCartDto result = shoppingCartService.saveNewCartItem(requestDto);

        assertEquals(mockCartDto, result);
        assertTrue(mockCart.getCartItems().contains(mockCartItem));
    }

    @Test
    @DisplayName("Delete cart item with valid input deletes and returns updated cart")
    public void deleteCartItem_Valid_DeletesAndReturnsUpdatedCart() {
        mockCart.setUser(mockUser);
        mockCartItem.setShoppingCart(mockCart);
        mockUser.setId(3L);

        when(userService.getUser()).thenReturn(mockUser);
        when(shoppingCartRepository.findByUserId(mockUser.getId()))
                .thenReturn(Optional.of(mockCart));
        when(shoppingCartMapper.toDto(mockCart)).thenReturn(mockCartDto);
        Long cartItemId = 1L;

        ShoppingCartDto result = shoppingCartService.deleteCartItem(cartItemId);

        assertEquals(mockCartDto, result);
        assertFalse(mockCart.getCartItems().contains(mockCartItem));
    }

    @Test
    @DisplayName("Attempt to delete cart item not owned by user throws EntityNotFoundException")
    public void deleteCartItem_ItemNotOwnedByUser_ThrowsEntityNotFoundException() {
        User anotherUser = new User();
        mockCart.setUser(anotherUser);
        mockCartItem.setShoppingCart(mockCart);
        Long cartItemId = 1L;
        anotherUser.setId(2L);

        when(userService.getUser()).thenReturn(mockUser);

        assertThrows(EntityNotFoundException.class, ()
                -> shoppingCartService.deleteCartItem(cartItemId));
    }

    @Test
    @DisplayName("Update cart item quantity with valid input")
    public void updateQuantity_Valid_UpdatesQuantityAndReturnsCart() {
        Long cartItemId = 1L;
        when(userService.getUser()).thenReturn(mockUser);
        when(cartItemService.getById(cartItemId)).thenReturn(mockCartItem);
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(mockCartItem);
        when(shoppingCartMapper.toDto(any(ShoppingCart.class))).thenReturn(mockCartDto);
        when(shoppingCartRepository.findByUserId(mockUser.getId()))
                .thenReturn(Optional.of(mockCart));

        ShoppingCartDto result = shoppingCartService.updateQuantity(cartItemId, updateDto);

        assertEquals(updateDto.quantity(), mockCartItem.getQuantity());
        assertEquals(mockCartDto, result);
    }

    @Test
    @DisplayName("Attempt to get shopping cart for a user without one")
    public void getShoppingCart_UserWithoutCart_ThrowsEntityNotFoundException() {
        when(userService.getUser()).thenReturn(mockUser);
        when(shoppingCartRepository.findByUserId(mockUser.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> shoppingCartService.getShoppingCart());
    }

    @Test
    @DisplayName("Add the same cart item twice")
    public void addSameCartItemTwice_AddsNewEntry() {
        Long cartItemId = 1L;
        when(shoppingCartRepository.findByUserId(mockUser.getId()))
                .thenReturn(Optional.of(mockCart));
        when(userService.getUser()).thenReturn(mockUser);
        when(cartItemService.getById(cartItemId)).thenReturn(mockCartItem, new CartItem());

        shoppingCartService.updateQuantity(cartItemId, updateDto);
        shoppingCartService.updateQuantity(cartItemId, updateDto);

        verify(cartItemService, times(2)).getById(cartItemId);
        assertEquals(mockCartItem.getQuantity(), 5);
    }

    @Test
    @DisplayName("Get shopping cart model when cart exists")
    public void getShoppingCartModel_CartExists_ReturnsCart() {
        when(userService.getUser()).thenReturn(mockUser);
        when(shoppingCartRepository.findByUserId(mockUser.getId()))
                .thenReturn(Optional.of(mockCart));

        ShoppingCart result = shoppingCartService.getShoppingCartModel();

        assertEquals(mockCart, result);
    }

    @Test
    @DisplayName("Attempt to get shopping cart model when cart doesn't exist")
    public void getShoppingCartModel_CartDoesNotExist_ThrowsEntityNotFoundException() {
        when(userService.getUser()).thenReturn(mockUser);
        when(shoppingCartRepository.findByUserId(mockUser.getId()))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, ()
                -> shoppingCartService.getShoppingCartModel());
    }

    @Test
    @DisplayName("Confirm purchase by deleting old cart and creating a new one")
    public void clearAndCreateNewShoppingCart_Valid_DeletesAndCreatesNewCart() {
        mockCart.setUser(mockUser);

        shoppingCartService.clearAndCreateNewShoppingCart(mockCart);

        verify(shoppingCartRepository, times(1)).delete(mockCart);
        ArgumentCaptor<ShoppingCart> cartCaptor = ArgumentCaptor.forClass(ShoppingCart.class);
        verify(shoppingCartRepository, times(1)).save(cartCaptor.capture());

        ShoppingCart savedCart = cartCaptor.getValue();
        assertEquals(mockUser, savedCart.getUser());
    }
}
