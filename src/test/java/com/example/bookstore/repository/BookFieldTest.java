package com.example.bookstore.repository;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.bookstore.repository.book.BookField;
import java.util.Arrays;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BookFieldTest {

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
}
