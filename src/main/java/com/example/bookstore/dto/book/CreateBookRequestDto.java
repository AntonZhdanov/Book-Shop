package com.example.bookstore.dto.book;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.ISBN;

@Accessors(chain = true)
public record CreateBookRequestDto(
        @NotEmpty String title,
        @NotEmpty String author,
        @NotNull @ISBN String isbn,
        @NotNull @Positive BigDecimal price,
        @NotEmpty String description,
        @NotEmpty String coverImage,
        List<Long> categoryIds
) {}
