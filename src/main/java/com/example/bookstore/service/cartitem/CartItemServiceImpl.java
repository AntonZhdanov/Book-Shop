package com.example.bookstore.service.cartitem;

import com.example.bookstore.dto.cartitem.CreateCartItemRequestDto;
import com.example.bookstore.exception.EntityNotFoundException;
import com.example.bookstore.mapper.CartItemMapper;
import com.example.bookstore.model.Book;
import com.example.bookstore.model.CartItem;
import com.example.bookstore.model.ShoppingCart;
import com.example.bookstore.repository.book.BookRepository;
import com.example.bookstore.repository.cartitem.CartItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;
    private final CartItemMapper cartItemMapper;

    @Override
    public CartItem save(CreateCartItemRequestDto createCartItemRequestDto,
                         ShoppingCart shoppingCart) {
        Book book = bookRepository.findById(createCartItemRequestDto.getBookId())
                .orElseThrow(() -> new EntityNotFoundException("Book not found with ID: "
                        + createCartItemRequestDto.getBookId()));
        CartItem cartItem = cartItemMapper.toModel(createCartItemRequestDto);
        cartItem.setShoppingCart(shoppingCart);
        cartItem.setBook(book);
        return cartItemRepository.save(cartItem);
    }

    @Override
    public CartItem getById(Long id) {
        return cartItemRepository.findById(id).orElseThrow(()
                -> new EntityNotFoundException("Can't find cartItem by id: " + id));
    }

    @Override
    public void deleteById(Long id) {
        if (!cartItemRepository.existsById(id)) {
            throw new EntityNotFoundException("Can't delete cartItem by id: " + id);
        }
        cartItemRepository.deleteById(id);
    }
}
