package com.example.bookstore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.example.bookstore.dto.book.BookDto;
import com.example.bookstore.dto.book.BookDtoWithoutCategoryIds;
import com.example.bookstore.dto.book.BookSearchParametersDto;
import com.example.bookstore.dto.book.CreateBookRequestDto;
import com.example.bookstore.dto.category.CategoryDto;
import com.example.bookstore.exception.EntityNotFoundException;
import com.example.bookstore.mapper.BookMapper;
import com.example.bookstore.model.Book;
import com.example.bookstore.model.Category;
import com.example.bookstore.repository.book.BookRepository;
import com.example.bookstore.repository.book.BookSpecificationBuilder;
import com.example.bookstore.service.book.BookServiceImpl;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {
    @InjectMocks
    private BookServiceImpl bookService;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookMapper bookMapper;
    @Mock
    private BookSpecificationBuilder specificationBuilder;

    @Test
    @DisplayName("Save a book")
    public void save_ValidCreateBookRequestDto_ReturnsValidBookDto() {
        // Given
        Book book = new Book();
        book.setTitle("The Lord of the Rings");
        book.setAuthor("J. R. R. Tolkien");
        book.setIsbn("9780544003415");
        book.setPrice(BigDecimal.valueOf(15.5));
        book.setDescription("Awesome book");
        book.setCoverImage("The Lord of the Rings image");

        List<Long> categoryIds = List.of(1L);

        BookDto bookDto = new BookDto();
        bookDto.setId(1L);
        bookDto.setTitle("The Lord of the Rings");
        bookDto.setAuthor("J. R. R. Tolkien");
        bookDto.setIsbn("9780544003415");
        bookDto.setPrice(BigDecimal.valueOf(15.5));
        bookDto.setDescription("Awesome book");
        bookDto.setCoverImage("The Lord of the Rings image");
        bookDto.setCategoryIds(categoryIds);

        CreateBookRequestDto requestDto = new CreateBookRequestDto(
                "The Lord of the Rings",
                "J. R. R. Tolkien",
                "9780544003415",
                BigDecimal.valueOf(15.5),
                "Awesome book",
                "The Lord of the Rings image",
                 categoryIds
        );

        when(bookMapper.toModel(requestDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        // When
        BookDto actual = bookService.save(requestDto);

        // Then
        assertEquals(bookDto, actual);
        verify(bookRepository, times(1)).save(book);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Find all books")
    public void findAll_ValidPageable_ReturnsAllBooks() {
        // Given
        Long bookId = 1L;
        Book book = new Book();
        book.setId(bookId);
        book.setTitle("The Lord of the Rings");
        book.setAuthor("J. R. R. Tolkien");
        book.setIsbn("9780544003415");
        book.setPrice(BigDecimal.valueOf(15.5));
        book.setDescription("Awesome book");
        book.setCoverImage("The Lord of the Rings image");

        Category fantasyCategory = new Category();
        fantasyCategory.setId(1L);
        fantasyCategory.setName("Fantasy");
        fantasyCategory.setDescription("Fantasy");

        book.setCategories(new HashSet<>(List.of(fantasyCategory)));

        BookDto bookDto = new BookDto();
        bookDto.setId(book.getId());
        bookDto.setTitle(book.getTitle());
        bookDto.setAuthor(book.getAuthor());
        bookDto.setIsbn(book.getIsbn());
        bookDto.setPrice(book.getPrice());
        bookDto.setDescription(book.getDescription());
        bookDto.setCoverImage(book.getCoverImage());

        CategoryDto fairytaleCategoryDto = new CategoryDto();
        fairytaleCategoryDto.setId(fantasyCategory.getId());
        fairytaleCategoryDto.setName(fantasyCategory.getName());
        fairytaleCategoryDto.setDescription(fantasyCategory.getDescription());

        bookDto.setCategoryIds(List.of(fairytaleCategoryDto.getId()));

        Pageable pageable = PageRequest.of(0, 10);
        List<Book> books = List.of(book);
        Page<Book> bookPage = new PageImpl<>(books, pageable, books.size());

        when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        // When
        List<BookDto> bookDtos = bookService.findAll(pageable);

        // Then
        assertThat(bookDtos).hasSize(1);
        assertEquals(bookDtos.get(0), bookDto);
        verify(bookRepository, times(1)).findAll(pageable);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Get a book by id")
    public void getBookById_ValidBookId_ReturnsValidBookDto() {
        // Given
        Long bookId = 1L;
        Book book = new Book();
        book.setId(bookId);
        book.setTitle("The Lord of the Rings");
        book.setAuthor("J. R. R. Tolkien");
        book.setIsbn("9780544003415");
        book.setPrice(BigDecimal.valueOf(15.5));
        book.setDescription("Awesome book");
        book.setCoverImage("The Lord of the Rings image");

        Category fantasyCategory = new Category();
        fantasyCategory.setId(1L);
        fantasyCategory.setName("Fantasy");
        fantasyCategory.setDescription("Fantasy");

        book.setCategories(new HashSet<>(List.of(fantasyCategory)));

        CategoryDto fantasyCategoryDto = new CategoryDto();
        fantasyCategoryDto.setId(fantasyCategory.getId());
        fantasyCategoryDto.setName(fantasyCategory.getName());
        fantasyCategoryDto.setDescription(fantasyCategory.getDescription());

        BookDto bookDto = new BookDto();
        bookDto.setTitle(book.getTitle());
        bookDto.setAuthor(book.getAuthor());
        bookDto.setIsbn(book.getIsbn());
        bookDto.setPrice(book.getPrice());
        bookDto.setDescription(book.getDescription());
        bookDto.setCoverImage(book.getCoverImage());
        bookDto.setCategoryIds(List.of(fantasyCategoryDto.getId()));

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        // When
        BookDto actual = bookService.findById(bookId);

        // Then
        assertEquals(bookDto, actual);
        verify(bookRepository, times(1)).findById(bookId);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Get a book with not valid id")
    public void getBookById_NotValidBookId_ThrowsException() {
        // Given
        Long bookId = 100L;
        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When
        Exception exception = assertThrows(EntityNotFoundException.class, () ->
                bookService.findById(bookId));

        // Then
        String expected = "Can't find book by id: " + bookId;
        String actual = exception.getMessage();
        assertEquals(expected, actual);
        verify(bookRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("Update a book")
    public void update_ValidBook_ReturnsUpdatedBookDto() {
        // Given
        Long bookId = 1L;
        Book existingBook = new Book();
        existingBook.setId(bookId);
        existingBook.setTitle("The Lord of the Rings");
        existingBook.setAuthor("J. R. R. Tolkien");
        existingBook.setIsbn("9780544003415");
        existingBook.setPrice(BigDecimal.valueOf(15.5));
        existingBook.setDescription("Awesome book");
        existingBook.setCoverImage("The Lord of the Rings image");

        CreateBookRequestDto requestDto = new CreateBookRequestDto(
                "The Lord of the Rings updated",
                "J. R. R. Tolkien",
                "9780544003415-updated",
                BigDecimal.valueOf(19.5),
                "Awesome updated book",
                "The Lord of the Rings updated image",
                List.of(1L)
        );

        Book updatedBook = new Book();
        updatedBook.setId(bookId);
        updatedBook.setTitle(requestDto.title());
        updatedBook.setAuthor(requestDto.author());
        updatedBook.setIsbn(requestDto.isbn());
        updatedBook.setPrice(requestDto.price());
        updatedBook.setDescription(requestDto.description());
        updatedBook.setCoverImage(requestDto.coverImage());

        BookDto expectedDto = new BookDto();
        expectedDto.setId(updatedBook.getId());
        expectedDto.setTitle(updatedBook.getTitle());
        expectedDto.setAuthor(updatedBook.getAuthor());
        expectedDto.setIsbn(updatedBook.getIsbn());
        expectedDto.setPrice(updatedBook.getPrice());
        expectedDto.setDescription(updatedBook.getDescription());
        expectedDto.setCoverImage(updatedBook.getCoverImage());

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));
        when(bookRepository.save(updatedBook)).thenReturn(updatedBook);
        when(bookMapper.toDto(updatedBook)).thenReturn(expectedDto);

        // When
        BookDto resultDto = bookService.update(updatedBook);

        // Then
        assertEquals(expectedDto, resultDto);
        verify(bookRepository, times(1)).findById(bookId);
        verify(bookRepository, times(1)).save(updatedBook);
        verify(bookMapper, times(1)).toDto(updatedBook);
        verifyNoMoreInteractions(bookRepository, bookMapper);

    }

    @Test
    @DisplayName("Delete book by id")
    public void deleteById_ValidId_CallsRepositoryDelete() {
        // Given
        Long bookId = 1L;

        // When
        bookService.deleteById(bookId);

        // Then
        verify(bookRepository, times(1)).deleteById(bookId);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("Search books")
    public void search_ValidParameters_ReturnsListOfBooks() {
        // Given
        Long bookId = 1L;
        Book book = new Book();
        book.setId(bookId);
        book.setTitle("The Lord of the Rings");
        book.setAuthor("J. R. R. Tolkien");
        book.setIsbn("9780544003415");
        book.setPrice(BigDecimal.valueOf(15.5));
        book.setDescription("Awesome book");
        book.setCoverImage("The Lord of the Rings image");

        Category fantasyCategory = new Category();
        fantasyCategory.setId(1L);
        fantasyCategory.setName("Fantasy");
        fantasyCategory.setDescription("Fantasy");

        CategoryDto fantasyCategoryDto = new CategoryDto();
        fantasyCategoryDto.setId(fantasyCategory.getId());
        fantasyCategoryDto.setName(fantasyCategory.getName());
        fantasyCategoryDto.setDescription(fantasyCategory.getDescription());

        BookDto bookDto = new BookDto();
        bookDto.setId(book.getId());
        bookDto.setTitle(book.getTitle());
        bookDto.setAuthor(book.getAuthor());
        bookDto.setIsbn(book.getIsbn());
        bookDto.setPrice(book.getPrice());
        bookDto.setDescription(book.getDescription());
        bookDto.setCoverImage(book.getCoverImage());
        bookDto.setCategoryIds(List.of(fantasyCategoryDto.getId()));

        BookSearchParametersDto parameters = new BookSearchParametersDto(
                new String[]{"nonexistent author"},
                new String[]{"nonexistent title"},
                new String[]{""},
                new String[]{""},
                new String[]{""},
                new String[]{""});

        Specification<Book> spec = (root, query, criteriaBuilder) -> null;

        when(specificationBuilder.build(parameters)).thenReturn(spec);
        when(bookRepository.findAll(spec)).thenReturn(List.of(book));
        when(bookMapper.toDto(any(Book.class))).thenReturn(bookDto);

        // When
        List<BookDto> searchedBooks = bookService.search(parameters);

        // Then
        assertThat(searchedBooks).hasSize(1);
        assertEquals(bookDto, searchedBooks.get(0));
        verify(bookRepository, times(1)).findAll(spec);
        verifyNoMoreInteractions(bookRepository, specificationBuilder, bookMapper);
    }

    @Test
    @DisplayName("Search books with not valid parameters")
    public void search_NotValidParameters_ReturnsEmptyList() {
        // Given

        Long bookId = 1L;
        Book book = new Book();
        book.setId(bookId);
        book.setTitle("The Lord of the Rings");
        book.setAuthor("J. R. R. Tolkien");
        book.setIsbn("9780544003415");
        book.setPrice(BigDecimal.valueOf(15.5));
        book.setDescription("Awesome book");
        book.setCoverImage("The Lord of the Rings image");

        Category fantasyCategory = new Category();
        fantasyCategory.setId(1L);
        fantasyCategory.setName("Fantasy");
        fantasyCategory.setDescription("Fantasy");

        CategoryDto fantasyCategoryDto = new CategoryDto();
        fantasyCategoryDto.setId(fantasyCategory.getId());
        fantasyCategoryDto.setName(fantasyCategory.getName());
        fantasyCategoryDto.setDescription(fantasyCategory.getDescription());

        BookDto bookDto = new BookDto();
        bookDto.setId(book.getId());
        bookDto.setTitle(book.getTitle());
        bookDto.setAuthor(book.getAuthor());
        bookDto.setIsbn(book.getIsbn());
        bookDto.setPrice(book.getPrice());
        bookDto.setDescription(book.getDescription());
        bookDto.setCoverImage(book.getCoverImage());
        bookDto.setCategoryIds(List.of(fantasyCategoryDto.getId()));

        BookSearchParametersDto parameters = new BookSearchParametersDto(
                new String[]{"nonexistent author"},
                new String[]{"nonexistent title"},
                new String[]{""},
                new String[]{""},
                new String[]{""},
                new String[]{""});

        Specification<Book> spec = (root, query, criteriaBuilder) -> null;

        when(specificationBuilder.build(parameters)).thenReturn(spec);
        when(bookRepository.findAll(spec)).thenReturn(List.of());

        // When
        List<BookDto> searchedBooks = bookService.search(parameters);

        // Then
        assertThat(searchedBooks).hasSize(0);
        verify(bookRepository, times(1)).findAll(spec);
        verifyNoMoreInteractions(bookRepository, specificationBuilder, bookMapper);
    }

    @Test
    @DisplayName("Get books by category id")
    public void getBookByCategoryId_ValidCategoryId_ReturnsListOfBooks() {
        // Given
        Long bookId = 1L;
        Book book = new Book();
        book.setId(bookId);
        book.setTitle("The Lord of the Rings");
        book.setAuthor("J. R. R. Tolkien");
        book.setIsbn("9780544003415");
        book.setPrice(BigDecimal.valueOf(15.5));
        book.setDescription("Awesome book");
        book.setCoverImage("The Lord of the Rings image");

        Category fantasyCategory = new Category();
        fantasyCategory.setId(1L);
        fantasyCategory.setName("Fantasy");
        fantasyCategory.setDescription("Fantasy");

        book.setCategories(new HashSet<>(List.of(fantasyCategory)));

        CategoryDto fantasyCategoryDto = new CategoryDto();
        fantasyCategoryDto.setId(fantasyCategory.getId());
        fantasyCategoryDto.setName(fantasyCategory.getName());
        fantasyCategoryDto.setDescription(fantasyCategory.getDescription());

        BookDtoWithoutCategoryIds bookDto = new BookDtoWithoutCategoryIds();
        bookDto.setId(book.getId());
        bookDto.setTitle(book.getTitle());
        bookDto.setAuthor(book.getAuthor());
        bookDto.setIsbn(book.getIsbn());
        bookDto.setPrice(book.getPrice());
        bookDto.setDescription(book.getDescription());
        bookDto.setCoverImage(book.getCoverImage());

        when(bookRepository.findAllByCategoryId(anyLong())).thenReturn(List.of(book));
        when(bookMapper.toDtoWithoutCategoryIds(book)).thenReturn(bookDto);

        // When
        List<BookDtoWithoutCategoryIds> booksByCategoryId = bookService.getBooksByCategoryId(1L);

        // Then
        assertThat(booksByCategoryId).hasSize(1);
        assertEquals(bookDto, booksByCategoryId.get(0));

        verify(bookRepository, times(1)).findAllByCategoryId(anyLong());
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Get books by not valid category id")
    public void getBookByCategoryId_NotValidCategoryId_ReturnsEmptyList() {
        // Given
        when(bookRepository.findAllByCategoryId(anyLong())).thenReturn(List.of());

        // When
        List<BookDtoWithoutCategoryIds> booksByCategoryId = bookService.getBooksByCategoryId(1L);

        // Then
        assertThat(booksByCategoryId).hasSize(0);
        verify(bookRepository, times(1)).findAllByCategoryId(anyLong());
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }
}
