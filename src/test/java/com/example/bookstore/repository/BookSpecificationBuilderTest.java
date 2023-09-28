package com.example.bookstore.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.example.bookstore.dto.book.BookSearchParametersDto;
import com.example.bookstore.model.Book;
import com.example.bookstore.repository.book.BookSpecificationBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;

@SpringBootTest
class BookSpecificationBuilderTest {

    @Autowired
    private BookSpecificationBuilder builder;

    @Autowired
    private SpecificationProviderManager<Book> bookSpecificationProviderManager;

    @Test
    @DisplayName("Build Specifications with valid parameters")
    void build_ValidParameters_SpecificationIsBuilt() {
        // Given
        BookSearchParametersDto dto = new BookSearchParametersDto(
                new String[]{"The Great Gatsby"},
                new String[]{"F. Scott Fitzgerald"},
                new String[]{"9780141182636"},
                new String[]{"10.00"},
                new String[]{"A novel of the Jazz Age"},
                new String[]{"URL_TO_COVER_IMAGE"}
        );

        // When
        Specification<Book> spec = builder.build(dto);

        // Then
        assertNotNull(spec);
    }
}
