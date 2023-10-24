package com.example.bookstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.bookstore.dto.cartitem.CreateCartItemRequestDto;
import com.example.bookstore.exception.EntityNotFoundException;
import com.example.bookstore.mapper.CartItemMapper;
import com.example.bookstore.model.Book;
import com.example.bookstore.model.CartItem;
import com.example.bookstore.model.ShoppingCart;
import com.example.bookstore.repository.book.BookRepository;
import com.example.bookstore.repository.cartitem.CartItemRepository;
import com.example.bookstore.service.cartitem.CartItemServiceImpl;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CartItemServiceImplTest {
    @InjectMocks
    private CartItemServiceImpl cartItemServiceImpl;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private CartItemMapper cartItemMapper;
    @Mock
    private BookRepository bookRepository;

    private CreateCartItemRequestDto itemRequestDto;
    private ShoppingCart shoppingCart;
    private Book book;
    private CartItem cartItemMock;
    private Long bookId;
    private Long cartItemId;

    @BeforeEach
    public void setUp() {
        itemRequestDto = new CreateCartItemRequestDto();
        shoppingCart = new ShoppingCart();
        book = new Book();
        cartItemMock = new CartItem();
        itemRequestDto.setBookId(1L);
        itemRequestDto.setQuantity(1);
        bookId = 1L;
        cartItemId = 1L;
    }

    @Test
    @DisplayName("Save with valid parameters results in a successful save")
    void save_ValidParameters_SuccessfulSave() {
        when(bookRepository.findById(itemRequestDto.getBookId())).thenReturn(Optional.of(book));
        when(cartItemMapper.toModel(itemRequestDto)).thenReturn(cartItemMock);
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(i -> i.getArguments()[0]);

        CartItem result = cartItemServiceImpl.save(itemRequestDto, shoppingCart);

        assertEquals(book, result.getBook());
        assertEquals(shoppingCart, result.getShoppingCart());
    }

    @Test
    @DisplayName("Save with null BookId throws IllegalArgumentException")
    void save_NullBookId_ThrowsIllegalArgumentException() {
        itemRequestDto.setBookId(null);

        assertThrows(IllegalArgumentException.class, () ->
                cartItemServiceImpl.save(itemRequestDto, shoppingCart));
    }

    @Test
    @DisplayName("Save with valid parameters calls save method once")
    void save_ValidParameters_SaveCalledOnce() {
        itemRequestDto.setBookId(bookId);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(cartItemMapper.toModel(itemRequestDto)).thenReturn(cartItemMock);

        cartItemServiceImpl.save(itemRequestDto, shoppingCart);

        verify(cartItemRepository).save(any(CartItem.class));
    }

    @Test
    @DisplayName("Save with invalid BookId throws EntityNotFoundException")
    void save_InvalidBookId_ThrowsException() {
        itemRequestDto.setBookId(bookId);
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                cartItemServiceImpl.save(itemRequestDto, shoppingCart));
    }

    @Test
    @DisplayName("Find by valid ID returns CartItem")
    void findById_ValidId_ReturnsCartItem() {
        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.of(cartItemMock));

        CartItem result = cartItemServiceImpl.findById(cartItemId);

        assertEquals(cartItemMock, result);
    }

    @Test
    @DisplayName("Find by invalid ID throws EntityNotFoundException")
    void findById_InvalidId_ThrowsException() {
        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                cartItemServiceImpl.findById(cartItemId));
    }

    @Test
    @DisplayName("Delete by valid ID results in successful deletion")
    void deleteById_ValidId_SuccessfulDeletion() {
        when(cartItemRepository.existsById(cartItemId)).thenReturn(true);

        cartItemServiceImpl.deleteById(cartItemId);

        verify(cartItemRepository).deleteById(cartItemId);
    }

    @Test
    @DisplayName("Delete by invalid ID throws EntityNotFoundException")
    void deleteById_InvalidId_ThrowsException() {
        when(cartItemRepository.existsById(cartItemId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () ->
                cartItemServiceImpl.deleteById(cartItemId));
    }

    @Test
    @DisplayName("Delete by invalid ID ensures no deletion occurs")
    void deleteById_InvalidId_NoDeletion() {
        when(cartItemRepository.existsById(cartItemId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () ->
                cartItemServiceImpl.deleteById(cartItemId));
        verify(cartItemRepository, never()).deleteById(cartItemId);
    }
}
