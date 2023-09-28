package com.example.bookstore.dto.book;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.ISBN;

@Accessors(chain = true)
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
    @Positive
    private BigDecimal price;
    @NotEmpty
    private String description;
    @NotEmpty
    private String coverImage;
    private List<Long> categoryIds;
}
