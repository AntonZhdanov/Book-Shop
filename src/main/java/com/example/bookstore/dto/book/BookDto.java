package com.example.bookstore.dto.book;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.Data;
import org.hibernate.validator.constraints.ISBN;

@Data
public class BookDto {
    private Long id;
    @NotEmpty
    private String title;
    @NotEmpty
    private String author;
    @NotNull
    @ISBN
    private String isbn;
    @NotNull
    @Min(0)
    @Positive
    private BigDecimal price;
    @NotEmpty
    private String description;
    @NotEmpty
    private String coverImage;
}


