package com.example.bookstore.dto.book;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;
import org.hibernate.validator.constraints.ISBN;

public record CreateBookRequestDto(
        Long id,
        @NotEmpty String title,
        @NotEmpty String author,
        @NotNull @ISBN String isbn,
        @NotNull @Min(0) @Positive BigDecimal price,
        @NotEmpty String description,
        @NotEmpty String coverImage,
        List<Long> categoryIds
) {}
