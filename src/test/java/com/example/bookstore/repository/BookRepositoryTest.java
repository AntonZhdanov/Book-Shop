package com.example.bookstore.repository;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.bookstore.model.Book;
import com.example.bookstore.repository.book.BookField;
import com.example.bookstore.repository.book.BookRepository;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookRepositoryTest {
    @Autowired
    private BookRepository bookRepository;

    @Sql(scripts = {
            "classpath:database/categories/add-categories.sql",
            "classpath:database/books/add-books.sql",
            "classpath:database/categories/associate-categories-to-books.sql",
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/categories/remove-categories-from-books.sql",
            "classpath:database/categories/remove-categories.sql",
            "classpath:database/books/remove-books.sql",
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @DisplayName("Find all books by category id")
    void findAllByCategoryId_ValidCategoryIds_ReturnListOfBooks() {
        // When
        List<Book> fantasyBooks = bookRepository.findAllByCategoryId(1L);
        List<Book> scifiBooks = bookRepository.findAllByCategoryId(2L);
        List<Book> detectiveBooks = bookRepository.findAllByCategoryId(3L);

        // Then
        assertThat(fantasyBooks).hasSize(2);
        assertThat(scifiBooks).hasSize(1);
        assertThat(detectiveBooks).hasSize(1);
        assertEquals("1984", scifiBooks.get(0).getTitle());
        assertEquals("The Adventures of Sherlock Holmes", detectiveBooks.get(0).getTitle());
    }

    @Test
    @DisplayName("Test existence of specific field in BookRepository")
    void shouldHaveSpecificField() {
        // Given
        String expectedFieldName = "TITLE";

        // When
        boolean doesFieldExist = Arrays.stream(BookField.values())
                .anyMatch(field -> field.name().equals(expectedFieldName));

        // Then
        assertTrue(doesFieldExist, "Field with name " + expectedFieldName
                + " does not exist in BookField enum.");
    }

    @Test
    @DisplayName("Find all books by non-existent category id")
    void findAllByCategoryId_NonExistentId_EmptyList() {
        // When
        List<Book> books = bookRepository.findAllByCategoryId(999L);

        // Then
        assertTrue(books.isEmpty());
    }
}
